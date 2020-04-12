package com.apprisingsoftware.util;

public class Pair<T, U> extends Singlet<T> {
	
	protected final U second;
	protected final boolean commutative;
	
	public Pair(T first, U second, boolean commutative) {
		super(first);
		this.second = second;
		this.commutative = commutative;
	}
	public Pair(T first, U second) {
		this(first, second, false);
	}
	
	public U getSecond() {
		return second;
	}
	
	// Object
	@Override public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Pair<?, ?>)) return false;
		Pair<?, ?> other = (Pair<?, ?>)obj;
		return commutative ? equalsCommutative(first, second, other.first, other.second) : first.equals(other.first) && second.equals(other.second);
	}
	private static boolean equalsCommutative(Object a1, Object a2, Object b1, Object b2) {
		if (a1 == null) {
			if (a2 == null)
				return b1 == null && b2 == null;
			else
				return b1 == null && a2.equals(b2) || b2 == null && a2.equals(b1);
		}
		else {
			if (a2 == null)
				return b1 == null && a1.equals(b2) || b2 == null && a1.equals(b1);
			else
				return a1.equals(b1) && a2.equals(b2) || a1.equals(b2) && a2.equals(b1);
		}
	}
	@Override public int hashCode() {
		return (first == null ? 0 : first.hashCode()) + (second == null ? 0 : second.hashCode());
	}
	@Override public String toString() {
		return String.format("<%s, %s>", first, second);
	}
	
}
