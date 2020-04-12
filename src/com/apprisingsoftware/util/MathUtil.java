package com.apprisingsoftware.util;

import java.util.Random;

public final class MathUtil {
	
	private MathUtil() {}
	
	public static double random(double lower, double upper) {
		return lower + Math.random() * (upper - lower);
	}
	public static double nextDouble(Random random, double lower, double upper) {
		return lower + random.nextDouble() * (upper - lower);
	}
	
	public static int clamp(int lower, int var, int upper) {
		return Math.max(Math.min(var, upper), lower);
	}
	public static long clamp(long lower, long var, long upper) {
		return Math.max(Math.min(var, upper), lower);
	}
	public static float clamp(float lower, float var, float upper) {
		return Math.max(Math.min(var, upper), lower);
	}
	public static double clamp(double lower, double var, double upper) {
		return Math.max(Math.min(var, upper), lower);
	}
	
	public static int mod(int a, int q) {
		int r = a % q;
		return r < 0 ? r + q : r;
	}
	public static long mod(long a, long q) {
		long r = a % q;
		return r < 0 ? r + q : r;
	}
	public static float mod(float a, float q) {
		float r = a % q;
		return r < 0 ? r + q : r;
	}
	public static double mod(double a, double q) {
		double r = a % q;
		return r < 0 ? r + q : r;
	}
	
}
