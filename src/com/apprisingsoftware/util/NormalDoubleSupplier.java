package com.apprisingsoftware.util;

import java.util.function.DoubleSupplier;

public final class NormalDoubleSupplier {
	
	private final DoubleSupplier supplier;
	
	public NormalDoubleSupplier(DoubleSupplier supplier) {
		this.supplier = supplier;
	}
	
	public double get() {
		double val = supplier.getAsDouble();
		if (val < 0 || val > 1) throw new IllegalArgumentException();
		return val;
	}
	
}
