package com.apprisingsoftware.xmasrogue.util;

import com.apprisingsoftware.util.MathUtil;

public final class Coord {
	
	public final int x, y;
	
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coord plus(Coord other) {
		return new Coord(x + other.x, y + other.y);
	}
	public Coord plus(Dir Dir) {
		return this.plus(Dir.toCoord());
	}
	public Coord minus(Coord other) {
		return new Coord(x - other.x, y - other.y);
	}
	public Coord minus(Dir Dir) {
		return this.minus(Dir.toCoord());
	}
	public Coord dot(Coord other) {
		return new Coord(x * other.x, y * other.y);
	}
	public Coord dot(Dir Dir) {
		return this.dot(Dir.toCoord());
	}
	public Coord neg() {
		return new Coord(-x, -y);
	}
	
	public Coord rotate90() {
		return new Coord(-y, x);
	}
	public Coord rotate180() {
		return new Coord(-x, -y);
	}
	public Coord rotate270() {
		return new Coord(y, -x);
	}
	public Coord rotate90(Coord center) {
		return new Coord(-(y - center.y) + center.x, (x - center.x) + center.y);
	}
	public Coord rotate180(Coord center) {
		return new Coord(-(x - center.x) + center.x, -(y - center.y) + center.y);
	}
	public Coord rotate270(Coord center) {
		return new Coord((y - center.y) + center.x, -(x - center.x) + center.y);
	}
	public Coord rotate(int degrees) {
		switch (degrees) {
		case 0: return this;
		case 90: return rotate90();
		case 180: return rotate180();
		case 270: return rotate270();
		default: throw new AssertionError();
		}
	}
	public Coord rotate(Coord center, int degrees) {
		switch (degrees) {
		case 0: return this;
		case 90: return rotate90(center);
		case 180: return rotate180(center);
		case 270: return rotate270(center);
		default: throw new AssertionError();
		}
	}
	public Coord rotate(Coord center, Dir initialDir, Dir finalDir) {
		return rotate(center, MathUtil.mod(finalDir.getAngle() - initialDir.getAngle(), 360));
	}
	
	// Object
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coord other = (Coord) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	@Override public String toString() {
		return String.format("(%d,%d)", x, y);
	}
	
}
