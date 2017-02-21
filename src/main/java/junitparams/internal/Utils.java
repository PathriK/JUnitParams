package junitparams.internal;

import java.util.ArrayList;

/**
 * Some String utils to handle parameterised tests' results.
 *
 */
public class Utils {

    public static String[] splitAtCommaOrPipe(String input) {
        ArrayList<String> result = new ArrayList<String>();

        char character = '\0';
        char previousCharacter;

        StringBuilder value = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            previousCharacter = character;
            character = input.charAt(i);

            if (character == ',' || character == '|') {
                if (previousCharacter == '\\') {
                    value.setCharAt(value.length() - 1, character);
                    continue;
                }
                result.add(value.toString().trim());
                value = new StringBuilder();
                continue;
            }

            value.append(character);
        }
        result.add(value.toString().trim());

        return result.toArray(new String[]{});
    }

    static Object[] safelyCastParamsToArray(Object paramSet) {
        final Object[] params;
        if (paramSet instanceof Object[]) {
            params = (Object[]) paramSet;
        } else {
            params = new Object[]{paramSet};
        }
        return params;
    }

	public static String getTestCaseName(String name, Object paramSet, int i) {
		if(null == paramSet){
			paramSet = "null";
		}
		return name + "(" + paramSet.toString() + ") [" + i + "]";
	}
}
