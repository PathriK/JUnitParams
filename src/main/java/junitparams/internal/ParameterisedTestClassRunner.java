package junitparams.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * Testclass-level functionalities to handle parameters from a JUnit runner
 * class.
 *
 */
public class ParameterisedTestClassRunner {

    private Map<TestMethod, ParameterisedTestMethodRunner> parameterisedMethods = new HashMap<TestMethod, ParameterisedTestMethodRunner>();
    private Map<FrameworkMethod, TestMethod> testMethods = new HashMap<FrameworkMethod, TestMethod>();
    private List<TestMethod> testMethodsList;
    private List<FrameworkMethod> computeMethods;
    private List<FrameworkMethod> resultMethods;

    /**
     * Creates a runner for a given test class. Computes all the test methods
     * that are annotated as tests. Retrieves and caches all parameter values.
     *
     * @param testClass
     */
    public ParameterisedTestClassRunner(TestClass testClass) {
        computeTestMethods(testClass);
    }

    private void computeTestMethods(TestClass testClass) {
    	List<FrameworkMethod> annotatedMethods = testClass.getAnnotatedMethods(Test.class);  
    	testMethodsList = new ArrayList<TestMethod>();
    	computeMethods = new ArrayList<FrameworkMethod>();
    	resultMethods = new ArrayList<FrameworkMethod>();
                for (FrameworkMethod frameworkMethod : annotatedMethods){
                	TestMethod testMethod = new TestMethod(frameworkMethod, testClass);
                	testMethodsList.add(testMethod);
                	testMethods.put(frameworkMethod, testMethod);
                	cacheMethodRunner(testMethod);
                	addTestMethodForEachParamSet(computeMethods, testMethod);
                	resultMethods.add(testMethod.frameworkMethod());
                }         	
    }

    /**
     * Returns a list of <code>FrameworkMethod</code>s. Handles both
     * parameterised methods (counts them as many times as many paramsets they
     * have) and nonparameterised methods (just counts them once).
     *
     * @return a list of FrameworkMethod objects
     */
    public List<FrameworkMethod> computeFrameworkMethods() {
        return computeMethods;
    }

    /**
     * Returns a list of <code>FrameworkMethod</code>s - once per method, like
     * there were no parameters.
     * For JUnit to build names for IDE.
     */
    public List<FrameworkMethod> returnListOfMethods() {
        return resultMethods;
    }

    private void addTestMethodForEachParamSet(List<FrameworkMethod> resultMethods, TestMethod testMethod) {
    	if (testMethod.isParameterised() && !testMethod.isIgnored()){
            int paramSetSize = testMethod.parametersSets().length;
            for (int i = 0; i < paramSetSize; i++)
            	resultMethods.add(testMethod.frameworkMethod());    		
    		
    	}else{
    		resultMethods.add(testMethod.frameworkMethod());
    		
    	}
    }
    
     private void cacheMethodRunner(TestMethod testMethod) {
        if (!parameterisedMethods.containsKey(testMethod))
            parameterisedMethods.put(testMethod, new ParameterisedTestMethodRunner(testMethod));
    }

    /**
     * Returns a InvokeParameterisedMethod for parameterised methods and null
     * for nonparameterised
     *
     * @param method    Test method
     * @param testClass
     * @return a Statement with the invoker for the parameterised method
     */
    public Statement parameterisedMethodInvoker(FrameworkMethod method, Object testClass) {
        TestMethod testMethod = testMethods.get(method);

        if (!testMethod.isParameterised())
            return null;

        return buildMethodInvoker(method, testClass, testMethod);
    }

    private Statement buildMethodInvoker(FrameworkMethod method, Object testClass, TestMethod testMethod) {
        ParameterisedTestMethodRunner parameterisedMethod = parameterisedMethods.get(testMethod);

        return new InvokeParameterisedMethod(
                method, testClass, parameterisedMethod.currentParamsFromAnnotation(), parameterisedMethod.count());
    }

    /**
     * Tells if method should be run by this runner.
     *
     * @param testMethod
     * @return true, iff testMethod should be run by this runner.
     */
    public boolean shouldRun(TestMethod testMethod) {
        return testMethod.isParameterised();
    }

    /**
     * Executes parameterised method.
     *
     * @param method
     * @param methodInvoker
     * @param notifier
     */
    public void runParameterisedTest(TestMethod method, Statement methodInvoker, RunNotifier notifier) {
        parameterisedMethods.get(method).runTestMethod(methodInvoker, notifier);
    }

    /**
     * Returns description of a parameterised method.
     *
     * @param method TODO
     * @return Description of a method or null if it's not parameterised.
     */
    public Description describeParameterisedMethod(FrameworkMethod method) {
        TestMethod testMethod = testMethods.get(method);

        if (!testMethod.isParameterised())
            return null;

        return testMethod.describe();
    }

    /**
     * Returns a cached TestMethod object related to the given FrameworkMethod.
     * This object has all the params already retrieved, so use this one and not
     * TestMethod's constructor if you want to have everything retrieved once
     * and cached.
     *
     * @param method
     * @return a cached TestMethod instance
     */
    public TestMethod testMethodFor(FrameworkMethod method) {
        return testMethods.get(method);
    }

}
