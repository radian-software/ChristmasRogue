package com.apprisingsoftware.util;

public final class Perlin1 {
	
	private static final double INT_MAX_VALUE = Integer.MAX_VALUE + 1.0;
	
	private final int seed;
	private final int octaves;
	private final double persistence;
	
	public Perlin1(int seed, int octaves, double persistence) {
		this.seed = seed;
		this.octaves = octaves;
		this.persistence = persistence;
	}
	
	public double perlinNoise(double x) {
		double total = 0;
		for (int i=0; i<octaves; i++) {
			double frequency = Math.pow(2, i);
			double amplitude = Math.pow(persistence, i);
			total += interpolatedNoise(x * frequency) * amplitude;
		}
		return total;
	}
	public double getMinimumValue() {
		return 0;
	}
	public double getMaximumValue() {
		return (1 - Math.pow(persistence, octaves)) / (1 - persistence);
	}
	public double getExpectedValue() {
		return getMaximumValue() / 2;
	}
	
	/**
	 * Range: [0, 1]
	 * Expected: 0.5
	 */
	private double interpolatedNoise(double x) {
		int iPart = (int)Math.floor(x);
		double fPart = x - iPart;
		double left = smoothedNoise(iPart);
		double right = smoothedNoise(iPart + 1);
		return interpolateCosine(left, right, fPart);
	}
	/**
	 * Range: [left, right]
	 * Expected: N/A
	 */
	private double interpolateCosine(double left, double right, double xFrac) {
		double yFrac = (1 - Math.cos(xFrac * Math.PI)) / 2;
		return left + (right - left) * yFrac;
	}
	/**
	 * Range: [0, 1)
	 * Expected: 0.5
	 */
	private double smoothedNoise(int x) {
		return noise(x-1)/4 + noise(x)/2 + noise(x+1)/4;
	}
	/**
	 * Range: [0, 1)
	 * Expected: 0.5
	 */
	private double noise(int x) {
		x = x + seed * 37;
		x += seed * 37;
		x = (x << 13) ^ x;
		return ((x * (x * x * 15731 + 789221) + 1376312589) & 0x7FFFFFFF) / INT_MAX_VALUE;
	}
	
}
