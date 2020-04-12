package com.apprisingsoftware.util;

import java.util.function.Supplier;

/**
 * Represents a supplier of {@code boolean}-valued results.  This is the
 * {@code boolean}-producing primitive specialization of {@link Supplier}.
 *
 * <p>There is no requirement that a distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #getAsBool()}.
 *
 * @see Supplier
 */
@FunctionalInterface
public interface BoolSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    boolean getAsBool();
}
