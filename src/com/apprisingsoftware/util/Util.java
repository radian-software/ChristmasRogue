package com.apprisingsoftware.util;

public final class Util {
	
	private Util() {}
	
	@SafeVarargs public static <T> T firstNonNull(T... objs) {
		for (T obj : objs) if (obj != null) return obj;
		return null;
	}
	
}
