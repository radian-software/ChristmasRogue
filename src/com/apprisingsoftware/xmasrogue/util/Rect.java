package com.apprisingsoftware.xmasrogue.util;

public final class Rect {
	
	private final int x, y, width, height;
	
	public Rect(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getLeft() {
		return x;
	}
	public int getRight() {
		return x + width;
	}
	public int getTop() {
		return y;
	}
	public int getBottom() {
		return y + height;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	@Override public String toString() {
		return String.format("[(%d, %d), (%d, %d)][%dx%d]", getLeft(), getTop(), getRight(), getBottom(), getWidth(), getHeight());
	}
	
}
