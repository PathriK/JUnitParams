//TODO: Remove file
//package junitparams.internal.parameters;
//
//import static java.lang.String.format;
//
//import org.junit.runners.model.FrameworkMethod;
//
//import junitparams.Parameters;
//
//public class ParametersReader {
//
//    private final FrameworkMethod frameworkMethod;
//    private final Parameters parametersAnnotation;
//    
//    public ParametersReader(Class<?> testClass, FrameworkMethod frameworkMethod) {
//        this.frameworkMethod = frameworkMethod;
//        this.parametersAnnotation = frameworkMethod.getAnnotation(Parameters.class);     
//    }
//
//    public Object[] read() {
//        Object[] parameters = new Object[]{};
//            if (isApplicable()) {
//                parameters = getParameters();
//            }else{
//            	noStrategyFound();	
//            }            
//        return parameters;
//    }
//
//    private void noStrategyFound() {
//        throw new IllegalStateException(format("Method %s#%s is annotated with @Parameters but there were no parameters provided.",
//                frameworkMethod.getMethod().getDeclaringClass().getName(), frameworkMethod.getName()));
//    }
//    
//
//    public Object[] getParameters() {
//        return parametersAnnotation.value();
//    }
//    
//    public boolean isApplicable() {
//        return parametersAnnotation != null && parametersAnnotation.value().length > 0;
//    }    
//    
//}
