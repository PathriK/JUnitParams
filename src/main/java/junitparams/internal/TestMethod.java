package junitparams.internal;

import static java.lang.String.format;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import junitparams.Parameters;

/**
 * A wrapper for a test method
 *
 */
public class TestMethod {
    private FrameworkMethod frameworkMethod;
    private Class<?> testClass;
    private Object[] cachedParameters;
    private final Parameters parametersAnnotation;

    public TestMethod(FrameworkMethod method, TestClass testClass) {
        this.frameworkMethod = method;
        this.testClass = testClass.getJavaClass();
        this.parametersAnnotation = frameworkMethod.getAnnotation(Parameters.class);
    }

    public String name() {
        return frameworkMethod.getName();
    }

    @Override
    public int hashCode() {
        return frameworkMethod.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TestMethod)
                && hasTheSameNameAsFrameworkMethod((TestMethod) obj)
                && hasTheSameParameterTypesAsFrameworkMethod((TestMethod) obj);
    }

    private boolean hasTheSameNameAsFrameworkMethod(TestMethod testMethod) {
        return frameworkMethod.getName().equals(testMethod.frameworkMethod.getName());
    }

    private boolean hasTheSameParameterTypesAsFrameworkMethod(TestMethod testMethod) {
        Class<?>[] frameworkMethodParameterTypes = frameworkMethod.getMethod().getParameterTypes();
        Class<?>[] testMethodParameterTypes = testMethod.frameworkMethod.getMethod().getParameterTypes();
        return Arrays.equals(frameworkMethodParameterTypes, testMethodParameterTypes);
    }

    private Class<?> testClass() {
        return testClass;
    }

    public boolean isIgnored() {
        return hasIgnoredAnnotation() || hasNoParameters();
    }

    private boolean hasIgnoredAnnotation() {
        return frameworkMethod.getAnnotation(Ignore.class) != null;
    }

    private boolean hasNoParameters() {
       return isParameterised() && parametersSets().length == 0;
    }

    Description describe() {
    	if (!isIgnored() && !describeFlat()) {
            Description parametrised = Description.createSuiteDescription(name());
            Object[] params = parametersSets();
            for (int i = 0; i < params.length; i++) {
                Object paramSet = params[i];
                String name = Utils.getTestCaseName(name(),paramSet, i);

                parametrised.addChild(
                        Description.createTestDescription(testClass(), name)
                );
            }
            return parametrised;
        } else {
            return Description.createTestDescription(testClass(), name(), frameworkMethod.getAnnotations());
        }
    }

    private boolean describeFlat() {
        return System.getProperty("JUnitParams.flat") != null;
    }

    public Object[] parametersSets() {
        if (cachedParameters == null) {
            cachedParameters = read();
        }
        return cachedParameters;
    }

    public FrameworkMethod frameworkMethod() {
        return frameworkMethod;
    }

    boolean isParameterised() {
    	return frameworkMethod.getAnnotation(Parameters.class) != null;
    }
    
    private Object[] read() {
        Object[] parameters = new Object[]{};
            if (isApplicable()) {
                parameters = getParameters();
            }else{
            	noStrategyFound();	
            }            
        return parameters;
    }    
    
    private void noStrategyFound() {
        throw new IllegalStateException(format("Method %s#%s is annotated with @Parameters but there were no parameters provided.",
                frameworkMethod.getMethod().getDeclaringClass().getName(), frameworkMethod.getName()));
    }
    

    private Object[] getParameters() {
        return parametersAnnotation.value();
    }
    
    private boolean isApplicable() {
        return parametersAnnotation != null && parametersAnnotation.value().length > 0;
    }       
}
