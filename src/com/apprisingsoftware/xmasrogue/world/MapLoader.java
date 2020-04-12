package com.apprisingsoftware.xmasrogue.world;

public final class MapLoader implements MapBuilder {
	
	private MapTile[][] map;
	
	public MapLoader(MapTile[][] map) {
		this.map = map;
	}
	
	@Override public MapTile[][] getMap() {
		return map;
	}
	
	@Override public int getWidth() {
		return map.length;
	}
	
	@Override public int getHeight() {
		return map[0].length;
	}
	
}
