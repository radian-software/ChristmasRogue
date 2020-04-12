package com.apprisingsoftware.util;

public class Quadruplet<T, U, V, W> extends Triplet<T, U, V> {
	
	protected final W fourth;
	
	public Quadruplet(T first, U second, V third, W fourth) {
		super(first, second, third);
		this.fourth = fourth;
	}
	
	public W getFourth() {
		return fourth;
	}
	
	// Object
	@Override public String toString() {
		return String.format("<%s, %s, %s, %s>", first, second, third, fourth);
	}
	
}
