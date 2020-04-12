package com.apprisingsoftware.util;

import java.util.function.BiFunction;

/**
 * Represents a function that accepts two int-valued arguments and produces a
 * result.  This is the {@code int}-consuming primitive specialization for
 * {@link BiFunction}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(int, int)}.
 *
 * @param <R> the type of the result of the function
 *
 * @see BiFunction
 */
@FunctionalInterface
public interface BiIntFunction<R> {

    /**
     * Applies this function to the given argument.
     *
     * @param value1 the first function argument
     * @param value2 the second function argument
     * @return the function result
     */
    R apply(int value1, int value2);
}
