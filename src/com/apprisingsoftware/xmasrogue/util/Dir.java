package com.apprisingsoftware.xmasrogue.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class Dir {
	
	public static final Dir LEFT = new Dir(-1, 0);
	public static final Dir RIGHT = new Dir(1, 0);
	public static final Dir UP = new Dir(0, -1);
	public static final Dir DOWN = new Dir(0, 1);
	public static final Dir UPLEFT = new Dir(-1, -1);
	public static final Dir UPRIGHT = new Dir(1, -1);
	public static final Dir DOWNLEFT = new Dir(-1, 1);
	public static final Dir DOWNRIGHT = new Dir(1, 1);
	public static final List<Dir> ALL_COMPASS = Collections.unmodifiableList(Arrays.asList(LEFT, RIGHT, UP, DOWN, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT));
	public static final List<Dir> ORTHOGONAL_COMPASS = Collections.unmodifiableList(Arrays.asList(LEFT, RIGHT, UP, DOWN));
	
	private final int x, y;
	
	public Dir(int x, int y) {
		if (x == 0 && y == 0 || Math.abs(x) > 1 || Math.abs(y) > 1) throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
	}
	public Dir(Coord coord) {
		this(coord.x, coord.y);
	}
	
	public boolean isOrthogonal() {
		return x != 0 ^ y != 0;
	}
	public boolean isDiagonal() {
		return !isOrthogonal();
	}
	
	public int getOffset(int dimension) {
		if (dimension < 0 || dimension > 1) throw new IllegalArgumentException();
		return dimension == 0 ? x : y;
	}
	public int getX() {
		return getOffset(0);
	}
	public int getY() {
		return getOffset(1);
	}
	public boolean isHorizontal() {
		return y == 0;
	}
	public boolean isVertical() {
		return x == 0;
	}
	public boolean isPositive() {
		if (isOrthogonal()) return x == 1 || y == 1;
		throw new IllegalStateException("cannot determine sign of a diagonal Dir");
	}
	public Coord toCoord() {
		return new Coord(getOffset(0), getOffset(1));
	}
	public Coord toX() {
		return new Coord(getOffset(0), 0);
	}
	public Coord toY() {
		return new Coord(0, getOffset(1));
	}
	public int getAngle() {
		switch (x) {
		case -1: switch (y) {
		case -1: return 135;
		case 0: return 180;
		case 1: return 225;
		}
		case 0: switch (y) {
		case -1: return 90;
		case 1: return 270;
		}
		case 1: switch (y) {
		case -1: return 45;
		case 0: return 0;
		case 1: return 315;
		}
		}
		throw new AssertionError();
	}
	
	public Dir negative() {
		return new Dir(-x, -y);
	}
	public Dir turn(boolean counterclockwise) {
		if (counterclockwise) {
			switch (x) {
			case -1: switch (y) {
			case -1: return Dir.DOWNLEFT;
			case 0: return Dir.DOWN;
			case 1: return Dir.DOWNRIGHT;
			}
			case 0: switch (y) {
			case -1: return Dir.LEFT;
			case 1: return Dir.RIGHT;
			}
			case 1: switch (y) {
			case -1: return Dir.UPLEFT;
			case 0: return Dir.UP;
			case 1: return Dir.UPRIGHT;
			}
			}
			throw new AssertionError();
		}
		else {
			switch (x) {
			case -1: switch (y) {
			case -1: return Dir.UPRIGHT;
			case 0: return Dir.UP;
			case 1: return Dir.UPLEFT;
			}
			case 0: switch (y) {
			case -1: return Dir.RIGHT;
			case 1: return Dir.LEFT;
			}
			case 1: switch (y) {
			case -1: return Dir.DOWNRIGHT;
			case 0: return Dir.DOWN;
			case 1: return Dir.DOWNLEFT;
			}
			}
			throw new AssertionError();
		}
	}
	public Coord plus(Coord other) {
		return this.toCoord().plus(other);
	}
	public Coord minus(Coord other) {
		return this.toCoord().minus(other);
	}
	public Coord dot(Coord other) {
		return this.toCoord().dot(other);
	}
	public Coord plus(Dir other) {
		return this.plus(other.toCoord());
	}
	public Coord minus(Dir other) {
		return this.minus(other.toCoord());
	}
	public Coord dot(Dir other) {
		return this.dot(other.toCoord());
	}
	
	public static List<Coord> getAllNeighbors(Coord coord) {
		return Dir.ALL_COMPASS.stream().map(dir -> coord.plus(dir)).collect(Collectors.toList());
	}
	public static List<Coord> getAllNeighborsWithSelf(Coord coord) {
		List<Coord> neighbors = getAllNeighbors(coord);
		neighbors.add(coord);
		return neighbors;
	}
	public static List<Coord> getOrthogonalNeighbors(Coord coord) {
		return Dir.ORTHOGONAL_COMPASS.stream().map(dir -> coord.plus(dir)).collect(Collectors.toList());
	}
	public static List<Coord> getOrthogonalNeighborsWithSelf(Coord coord) {
		List<Coord> neighbors = getOrthogonalNeighbors(coord);
		neighbors.add(coord);
		return neighbors;
	}
	
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dir other = (Dir) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	@Override public String toString() {
		return new Coord(x, y).toString();
	}
	
}
