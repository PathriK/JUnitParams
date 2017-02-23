package junitparams;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import junitparams.internal.ParameterisedTestClassRunner;
import junitparams.internal.TestMethod;

public class JUnitParamsRunner extends BlockJUnit4ClassRunner {

    private ParameterisedTestClassRunner parameterisedRunner;
    private Description description;

    public JUnitParamsRunner(Class<?> klass) throws InitializationError {
        super(klass);
        parameterisedRunner = new ParameterisedTestClassRunner(getTestClass());
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        for (Throwable throwable : errors)
            throwable.printStackTrace();
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
    	TestMethod testMethod = parameterisedRunner.testMethodFor(method);
    	if (handleIgnored(testMethod, notifier))
            return;

        if (parameterisedRunner.shouldRun(testMethod)){
            parameterisedRunner.runParameterisedTest(testMethod, methodBlock(method), notifier);
        }
        else{
            verifyMethodCanBeRunByStandardRunner(testMethod);
            super.runChild(method, notifier);
        }
    }

    private void verifyMethodCanBeRunByStandardRunner(TestMethod testMethod) {
        List<Throwable> errors = new ArrayList<Throwable>();
        testMethod.frameworkMethod().validatePublicVoidNoArg(false, errors);
        if (!errors.isEmpty()) {
            throw new RuntimeException(errors.get(0));
        }
    }

    private boolean handleIgnored(TestMethod testMethod, RunNotifier notifier) {        
        if (testMethod.isIgnored()){
        	notifier.fireTestIgnored(describeMethod(testMethod.frameworkMethod()));
        	return true;
        }
        return false;
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return parameterisedRunner.computeFrameworkMethods();
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        Statement methodInvoker = parameterisedRunner.parameterisedMethodInvoker(method, test);
        if (methodInvoker == null)
            methodInvoker = super.methodInvoker(method, test);

        return methodInvoker;
    }

    @Override
    public Description getDescription() {
        if (description == null) {
            description = Description.createSuiteDescription(getName(), getTestClass().getAnnotations());
            List<FrameworkMethod> resultMethods = getListOfMethods();

            for (FrameworkMethod method : resultMethods)
                description.addChild(describeMethod(method));
        }

        return description;
    }

    private List<FrameworkMethod> getListOfMethods() {
        List<FrameworkMethod> frameworkMethods = parameterisedRunner.returnListOfMethods();
        return frameworkMethods;
    }

    public Description describeMethod(FrameworkMethod method) {
        Description child = parameterisedRunner.describeParameterisedMethod(method);

        if (child == null)
            child = describeChild(method);

        return child;
    }
    
}
