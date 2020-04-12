package com.apprisingsoftware.xmasrogue.world;



public interface MapBuilder {
	
	/**
	 * Return a world map that will fill one MapScreen. Information
	 * about world generation should be provided in a constructor.
	 * In general, this method may return a different result on
	 * different calls, but not necessarily.
	 */
	MapTile[][] getMap();
	
	/**
	 * Return the width of world maps returned by this MapBuilder.
	 */
	int getWidth();
	/**
	 * Return the height of world maps returned by this MapBuilder.
	 */
	int getHeight();
	
}
