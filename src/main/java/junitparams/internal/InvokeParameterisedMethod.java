package junitparams.internal;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * JUnit invoker for parameterised test methods
 *
 */
class InvokeParameterisedMethod extends Statement {

	private final Object[] params;
	private final FrameworkMethod testMethod;
	private final Object testClass;
	private String uniqueMethodName;

	InvokeParameterisedMethod(FrameworkMethod testMethod, Object testClass, Object params, int paramSetIdx) {
		this.testMethod = testMethod;
		this.testClass = testClass;
		uniqueMethodName = Utils.getTestCaseName(testMethod.getName(), params, paramSetIdx - 1);
		uniqueMethodName = String.format("%s(%s)", uniqueMethodName, testClass.getClass().getName());
		try {
			if (params instanceof String)
				this.params = castParamsFromString((String) params);
			else {
				this.params = castParamsFromObjects(params);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object[] castParamsFromString(String params) {
		Object[] columns = null;
		try {
			columns = Utils.splitAtCommaOrPipe(params);
			columns = castParamsUsingConverters(columns);
		} catch (RuntimeException e) {
			new IllegalArgumentException(
					"Cannot parse parameters. Did you use ',' or '|' as column separator? " + params, e)
							.printStackTrace();
		}

		return columns;
	}

	private Object[] castParamsFromObjects(Object params) throws Exception {
		Object[] paramset = Utils.safelyCastParamsToArray(params);

		return castParamsUsingConverters(paramset);
	}

	private Object[] castParamsUsingConverters(Object[] columns) throws IllegalArgumentException{
		Class<?>[] expectedParameterTypes = testMethod.getMethod().getParameterTypes();

		Annotation[][] parameterAnnotations = testMethod.getMethod().getParameterAnnotations();
		verifySameSizeOfArrays(columns, expectedParameterTypes);
		columns = castAllParametersToProperTypes(columns, expectedParameterTypes, parameterAnnotations);
		return columns;
	}

	private Object[] castAllParametersToProperTypes(Object[] columns, Class<?>[] expectedParameterTypes,
			Annotation[][] parameterAnnotations) throws IllegalArgumentException {
		Object[] result = new Object[columns.length];

		for (int i = 0; i < columns.length; i++) {
			if (parameterAnnotations[i].length == 0)
				result[i] = castParameterDirectly(columns[i], expectedParameterTypes[i]);
			else
			throw new IllegalArgumentException("annotation is not supported in function arguments");
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private Object castParameterDirectly(Object object, Class clazz) {
		if (object == null || clazz.isInstance(object) || (!(object instanceof String) && clazz.isPrimitive()))
			return object;
		if (clazz.isEnum())
			return (Enum.valueOf(clazz, (String) object));
		if (clazz.isAssignableFrom(String.class))
			return object.toString();
		if (clazz.isAssignableFrom(Class.class))
			try {
				return Class.forName((String) object);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Parameter class (" + object + ") not found", e);
			}
		if (clazz.isAssignableFrom(Integer.TYPE) || clazz.isAssignableFrom(Integer.class))
			return Integer.parseInt((String) object);
		if (clazz.isAssignableFrom(Short.TYPE) || clazz.isAssignableFrom(Short.class))
			return Short.parseShort((String) object);
		if (clazz.isAssignableFrom(Long.TYPE) || clazz.isAssignableFrom(Long.class))
			return Long.parseLong((String) object);
		if (clazz.isAssignableFrom(Float.TYPE) || clazz.isAssignableFrom(Float.class))
			return Float.parseFloat((String) object);
		if (clazz.isAssignableFrom(Double.TYPE) || clazz.isAssignableFrom(Double.class))
			return Double.parseDouble((String) object);
		if (clazz.isAssignableFrom(Boolean.TYPE) || clazz.isAssignableFrom(Boolean.class))
			return Boolean.parseBoolean((String) object);
		if (clazz.isAssignableFrom(Character.TYPE) || clazz.isAssignableFrom(Character.class))
			return object.toString().charAt(0);
		if (clazz.isAssignableFrom(Byte.TYPE) || clazz.isAssignableFrom(Byte.class))
			return Byte.parseByte((String) object);
		if (clazz.isAssignableFrom(BigDecimal.class))
			return new BigDecimal((String) object);
		PropertyEditor editor = PropertyEditorManager.findEditor(clazz);
		if (editor != null) {
			editor.setAsText((String) object);
			return editor.getValue();
		}
		throw new IllegalArgumentException("Parameter type (" + clazz.getName() + ") cannot be handled!"
				+ " Only primitive types, BigDecimals and Strings can be used.");
	}

	private void verifySameSizeOfArrays(Object[] columns, Class<?>[] parameterTypes) {
		if (parameterTypes.length != columns.length)
			throw new IllegalArgumentException(
					"Number of parameters inside @Parameters annotation doesn't match the number of test method parameters.\nThere are "
							+ columns.length + " parameters in annotation, while there's " + parameterTypes.length
							+ " parameters in the " + testMethod.getName() + " method.");
	}

	boolean matchesDescription(Description description) {
		return description.hashCode() == uniqueMethodName.hashCode();
	}

	@Override
	public void evaluate() throws Throwable {
		testMethod.invokeExplosively(testClass, params == null ? new Object[] { params } : params);
	}

}
