package junitparams.internal.parameters;

import junitparams.Parameters;
import org.junit.runners.model.FrameworkMethod;

//class ParametersFromValue implements ParametrizationStrategy {
class ParametersFromValue {

    private final Parameters parametersAnnotation;

    ParametersFromValue(FrameworkMethod frameworkMethod) {
        parametersAnnotation = frameworkMethod.getAnnotation(Parameters.class);
    }

    public Object[] getParameters() {
        return parametersAnnotation.value();
    }
    
    public boolean isApplicable() {
        return parametersAnnotation != null && parametersAnnotation.value().length > 0;
    }
}