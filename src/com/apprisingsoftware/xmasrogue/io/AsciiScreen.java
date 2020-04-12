package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.xmasrogue.util.Coord;
import java.util.Collection;

public interface AsciiScreen {
	
	/**
	 * If 0 ≤ x < getWidth() and 0 ≤ y < getHeight(), and
	 * !isTransparent(x, y), then return the AsciiTile corresponding
	 * to that particular location on the screen. Otherwise, the
	 * behavior is undefined.
	 */
	AsciiTile getTile(int x, int y);
	/**
	 * If the location (x, y) is out of bounds, then return false.
	 * Otherwise, return true if and only if a character is intended
	 * to be rendered at (x, y) on this screen such that characters
	 * on screens behind it on a CascadedAsciiScreen will not be visible
	 * (even if the character is a space).
	 */
	boolean isTransparent(int x, int y);
	default boolean inBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
	}
	/**
	 * Return a collection of all locations (x, y) inside the bounds of
	 * this screen for which the return value of getTile(x, y) has changed
	 * since the last call to getUpdatedTiles(). All locations inside
	 * the bounds of the screen are required to be included in the set
	 * returned from the first call to getUpdatedTiles(). If getUpdatedTiles()
	 * is called twice without any other methods being called on the screen,
	 * the return value the second time will be an empty collection.
	 */
	Collection<Coord> getUpdatedTiles();
	
	/**
	 * Return the width of this screen.
	 */
	int getWidth();
	/**
	 * Return the height of this screen.
	 */
	int getHeight();
	
}
