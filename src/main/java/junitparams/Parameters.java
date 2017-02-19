package junitparams;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * THE annotation for the test parameters. Use it to say that a method takes
 * some parameters and define how to obtain them.
 *
 * @author Pawel Lipinski
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters {
    /**
     * Parameter values defined as a String array. Each element in the array is
     * a full parameter set, comma-separated or pipe-separated ('|').
     * The values must match the method parameters in order and type.
     * Whitespace characters are trimmed (use source class or method if You need to provide such parameters)
     * <p>
     * Example: <code>@Parameters({
     * "1, joe, 26.4, true",
     * "2, angie, 37.2, false"})</code>
     */
    String[] value() default {};
}
