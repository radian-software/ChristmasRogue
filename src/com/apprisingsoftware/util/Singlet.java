package com.apprisingsoftware.util;

public class Singlet<T> {
	
	protected T first;
	
	public Singlet(T first) {
		this.first = first;
	}
	
	public T getFirst() {
		return first;
	}
	
	// Object
	@Override public String toString() {
		return String.format("<%s>", first);
	}
	
}
