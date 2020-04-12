package com.apprisingsoftware.util;

public final class Perlin2 {
	
	private static final double INT_MAX_VALUE = Integer.MAX_VALUE + 1.0;
	
	private final int seed;
	private final int octaves;
	private final double persistence;
	private final double yStretch;
	
	public Perlin2(int seed, int octaves, double persistence, double yStretch) {
		this.seed = seed;
		this.octaves = octaves;
		this.persistence = persistence;
		this.yStretch = yStretch;
	}
	
	public double perlinNoise(double x, double y) {
		double total = 0;
		for (int i=0; i<octaves; i++) {
			double frequency = Math.pow(2, i);
			double amplitude = Math.pow(persistence, i);
			total += interpolatedNoise(x * frequency, y * frequency / yStretch) * amplitude;
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
	private double interpolatedNoise(double x, double y) {
		int iPartX = (int) Math.floor(x);
		double fPartX = x - iPartX;
		int iPartY = (int) Math.floor(y);
		double fPartY = y - iPartY;
		double leftFloor = smoothedNoise(iPartX, iPartY);
		double rightFloor = smoothedNoise(iPartX + 1, iPartY);
		double leftCeil = smoothedNoise(iPartX, iPartY + 1);
		double rightCeil = smoothedNoise(iPartX + 1, iPartY + 1);
		double midFloor = interpolateCosine(leftFloor, rightFloor, fPartX);
		double midCeil = interpolateCosine(leftCeil, rightCeil, fPartX);
		return interpolateCosine(midFloor, midCeil, fPartY);
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
	private double smoothedNoise(int x, int y) {
		return
				(noise(x-1, y-1) + noise(x-1, y+1) + noise(x+1, y-1) + noise(x+1, y+1)) / 16 +
				(noise(x, y-1) + noise(x, y+1) + noise(x-1, y) + noise(x+1, y)) / 8 +
				(noise(x, y)) / 4;
	}
	/**
	 * Range: [0, 1)
	 * Expected: 0.5
	 */
	private double noise(int x, int y) {
		x = x + y * 57 + seed * 37;
		x = (x << 13) ^ x;
		return ((x * (x * x * 15731 + 789221) + 1376312589) & 0x7FFFFFFF) / INT_MAX_VALUE;
	}
	
}
