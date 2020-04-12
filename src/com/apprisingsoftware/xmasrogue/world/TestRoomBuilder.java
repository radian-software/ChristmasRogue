package com.apprisingsoftware.xmasrogue.world;

import com.apprisingsoftware.xmasrogue.entity.Entities;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.util.Random;


public final class TestRoomBuilder implements MapBuilder {
	
	protected final int width, height, originalSeed, depth;
	
	public TestRoomBuilder(int width, int height, int depth, int originalSeed) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.originalSeed = originalSeed;
	}
	
	@Override public MapTile[][] getMap() {
		Random random = new Random((originalSeed * depth) ^ depth);
		MapTile[][] map = new MapTile[width][height];
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				if (x == 0 || y == 0 || x == width-1 || y == height-1) {
					map[x][y] = new MapTile(Terrain.DUNGEON_WALL);
				}
				else {
					map[x][y] = new MapTile(Terrain.DUNGEON_FLOOR);
				}
			}
		}
		if (depth > 0) {
			Coord upStaircaseLoc = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain() == Terrain.DUNGEON_FLOOR, random);
			map[upStaircaseLoc.x][upStaircaseLoc.y].setTerrain(Terrain.DOWNSTAIRS_S);
		}
		Coord downStaircaseLoc = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain() == Terrain.DUNGEON_FLOOR, random);
		map[downStaircaseLoc.x][downStaircaseLoc.y].setTerrain(Terrain.DOWNSTAIRS_S);
		for (int i=0; i<10; i++) {
			Coord enemyCoord = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain().isOpen() && tile.getEntity() == null, random);
			map[enemyCoord.x][enemyCoord.y].setEntity(Entities.RAT.make());
		}
		return map;
	}
	
	@Override public int getWidth() {
		return width;
	}
	@Override public int getHeight() {
		return height;
	}
	
}
