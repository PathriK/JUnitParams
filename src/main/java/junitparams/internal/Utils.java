package junitparams.internal;

import java.lang.reflect.*;

/**
 * Some String utils to handle parameterised tests' results.
 *
 * @author Pawel Lipinski
 */
public class Utils {
    public static final String REGEX_ALL_NEWLINES = "(\\r\\n|\\n|\\r)";

    public static String stringify(Object paramSet, int paramIdx) {
        String result = "[" + paramIdx + "] ";

        return result + stringify(paramSet);
    }

    public static String stringify(Object paramSet) {
        String result;
        if (paramSet == null)
            result = "null";
        else if (paramSet instanceof String)
            result = paramSet.toString();
        else
            result = asCsvString(safelyCastParamsToArray(paramSet));

        return trimSpecialChars(result);
    }

    public static String getParameterStringByIndexOrEmpty(Object paramSet, int parameterIndex) {
        Object[] params = safelyCastParamsToArray(paramSet);
        if (parameterIndex >= 0 && parameterIndex < params.length) {
            return addParamToResult("", params[parameterIndex]);
        }

        return "";
    }

    private static String trimSpecialChars(String result) {
        return result.replace('(', '[').replace(')', ']').replaceAll(REGEX_ALL_NEWLINES, " ");
    }

    static Object[] safelyCastParamsToArray(Object paramSet) {
        Object[] params;

        if (paramSet instanceof String[]) {
            params = new Object[]{paramSet};
        } else {
            try {
                params = (Object[]) paramSet;
            } catch (ClassCastException e) {
                params = new Object[]{paramSet};
            }
        }
        return params;
    }

    private static String asCsvString(Object[] params) {
        if (params == null)
            return "null";

        if (params.length == 0)
            return "";

        String result = "";

        for (int i = 0; i < params.length - 1; i++) {
            Object param = params[i];
            result = addParamToResult(result, param) + ", ";
        }
        result = addParamToResult(result, params[params.length - 1]);

        return result;
    }

    private static String addParamToResult(String result, Object param) {
        if (param == null)
            result += "null";
        else {
            try {
                tryFindingOverridenToString(param);
                result += param.toString();
            } catch (Exception e) {
                result += param.getClass().getSimpleName();
            }
        }
        return result;
    }

    private static void tryFindingOverridenToString(Object param)
            throws NoSuchMethodException {
        final Method toString = param.getClass().getMethod("toString");

        if (toString.getDeclaringClass().equals(Object.class)) {
            throw new NoSuchMethodException();
        }
    }
}
