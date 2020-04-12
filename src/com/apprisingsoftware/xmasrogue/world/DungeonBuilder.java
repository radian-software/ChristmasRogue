package com.apprisingsoftware.xmasrogue.world;

import com.apprisingsoftware.util.ArrayUtil;
import com.apprisingsoftware.util.MathUtil;
import com.apprisingsoftware.util.Pair;
import com.apprisingsoftware.util.Perlin2;
import com.apprisingsoftware.xmasrogue.entity.Armors;
import com.apprisingsoftware.xmasrogue.entity.Entities;
import com.apprisingsoftware.xmasrogue.entity.Foods;
import com.apprisingsoftware.xmasrogue.entity.Sleigh;
import com.apprisingsoftware.xmasrogue.entity.Weapons;
import com.apprisingsoftware.xmasrogue.util.Coord;
import com.apprisingsoftware.xmasrogue.util.Dir;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class DungeonBuilder implements MapBuilder {
	
	protected final int width, height, originalSeed, depth;
	
	private interface Feature {
		/*
		 * # Already-placed walls
		 * . Interior tile (randomly selected)
		 * + Proposed door tile (randomly selected)
		 * X (0, 0) for proposed feature
		 * ` Space occupied by proposed feature
		 * 
		 * ###
		 *   #
		 *   #````
		 *  .+X```
		 *   #````
		 * ###
		 * 
		 *     ````````
		 * ####````````
		 *   .+X```````
		 * ####````````
		 *     ````````
		 */
		List<Pair<Coord, Terrain>> getShiftedFeatureData();
		boolean needsBorders(Coord coord);
		
		default boolean canPlace(MapTile[][] map, Predicate<MapTile> freeTiles) {
			List<Pair<Coord, Terrain>> shiftedFeatureData = getShiftedFeatureData();
			if (shiftedFeatureData == null) return false;
			for (Pair<Coord, Terrain> pair : shiftedFeatureData) {
				Coord rootLoc = pair.getFirst();
				if (needsBorders(rootLoc)) {
					for (Coord loc : Dir.getOrthogonalNeighborsWithSelf(pair.getFirst())) { // allow space for neighbors
						if (loc.x < 0 || loc.y < 0 || loc.x >= map.length || loc.y >= map[loc.x].length ||
								!freeTiles.test(map[loc.x][loc.y])) {
							return false;
						}
					}
				}
				else {
					Coord loc = rootLoc;
					if (loc.x < 0 || loc.y < 0 || loc.x >= map.length || loc.y >= map[loc.x].length ||
							!freeTiles.test(map[loc.x][loc.y])) {
						return false;
					}
				}
			}
			return true;
		}
		default boolean place(MapTile[][] map) {
			List<Pair<Coord, Terrain>> shiftedFeatureData = getShiftedFeatureData();
			for (Pair<Coord, Terrain> pair : shiftedFeatureData) {
				map[pair.getFirst().x][pair.getFirst().y].setTerrain(pair.getSecond());
			}
			return true;
		}
		static List<Pair<Coord, Terrain>> shiftFeatureData(List<Pair<Coord, Terrain>> originalFeatureData, int doorX, int doorY, Dir direction) {
			int rotationAngle = MathUtil.mod(direction.getAngle() - Dir.RIGHT.getAngle(), 360);
			Coord doorLocation = new Coord(doorX, doorY);
			Coord featureOrigin = doorLocation.plus(direction);
			List<Pair<Coord, Terrain>> shiftedFeatureData = new ArrayList<>(originalFeatureData.size());
			for (int i=0; i<originalFeatureData.size(); i++) {
				Pair<Coord, Terrain> pair = originalFeatureData.get(i);
				shiftedFeatureData.add(new Pair<>(pair.getFirst().rotate(rotationAngle).plus(featureOrigin), pair.getSecond()));
			}
			return shiftedFeatureData;
		}
	}
	
	private static final class Room implements Feature {
		private final List<Pair<Coord, Terrain>> shiftedFeatureData;
		private final Coord doorLoc;
		public Room(int width, int height, int doorX, int doorY, Dir direction, int entranceOffset) {
			this.doorLoc = new Coord(doorX, doorY);
			
			List<Pair<Coord, Terrain>> featureData = ArrayUtil.cartesianProduct(width, height, (x, y) -> new Pair<>(new Coord(x, y - entranceOffset), Terrain.DUNGEON_FLOOR));
			
			this.shiftedFeatureData = Feature.shiftFeatureData(featureData, doorX, doorY, direction);
		}
		@Override public List<Pair<Coord, Terrain>> getShiftedFeatureData() {
			return shiftedFeatureData;
		}
		@Override public boolean needsBorders(Coord coord) {
			return !coord.equals(doorLoc);
		}
	}
	private static final class Corridor implements Feature {
		private final List<Pair<Coord, Terrain>> shiftedFeatureData;
		private final Dir lastDir;
		private final Coord doorLoc;
		public Corridor(int length, double turnChance, int doorX, int doorY, Dir direction, Random random) {
			this.doorLoc = new Coord(doorX, doorY);
			
			List<Pair<Coord, Terrain>> featureData = new ArrayList<>();
			Coord lastLoc = new Coord(0, 0);
			Dir lastDir = Dir.RIGHT;
			do {
				featureData.add(new Pair<>(lastLoc, Terrain.DUNGEON_FLOOR));
				lastLoc = lastLoc.plus(lastDir);
				if (random.nextDouble() < turnChance) {
					lastDir = lastDir.turn(random.nextBoolean());
				}
			}
			while (featureData.size() < length);
			
			this.shiftedFeatureData = Feature.shiftFeatureData(featureData, doorX, doorY, direction);
			this.lastDir = lastDir;
		}
		public Dir getLastDir() {
			return lastDir;
		}
		@Override public List<Pair<Coord, Terrain>> getShiftedFeatureData() {
			return shiftedFeatureData;
		}
		@Override public boolean needsBorders(Coord coord) {
			return !coord.equals(doorLoc);
		}
	}
	private static final class CorridorWithRoom implements Feature {
		private final List<Pair<Coord, Terrain>> shiftedFeatureData;
		private final Coord doorLoc;
		public CorridorWithRoom(int length, double turnChance, int width, int height, int doorX, int doorY, Dir direction, Random random) {
			this.doorLoc = new Coord(doorX, doorY);
			
			boolean addDoor1 = random.nextInt(100) < 60 || length >= 5;
			boolean addDoor2 = random.nextInt(100) < 60 || length >= 5;
			if (addDoor1 && addDoor2 && length < 4) {
				if (random.nextBoolean()) addDoor1 = false;
				else addDoor2 = false;
			}
			// Create corridor
			Corridor corridor = new Corridor(length, turnChance, doorX, doorY, direction, random);
			List<Pair<Coord, Terrain>> corridorFeatureData = corridor.getShiftedFeatureData();
			List<Pair<Coord, Terrain>> shiftedFeatureData = new ArrayList<>(corridorFeatureData);
			// Insert door
			int doorIndex = shiftedFeatureData.size() - 1;
			Coord doorLoc = shiftedFeatureData.get(doorIndex).getFirst();
			shiftedFeatureData.set(doorIndex, new Pair<>(doorLoc, addDoor2 ? Terrain.WOOD_DOOR : Terrain.DUNGEON_FLOOR));
			// Create room
			Room room = new Room(width, height, doorLoc.x, doorLoc.y, corridor.getLastDir(), random.nextInt(height-1));
			List<Pair<Coord, Terrain>> roomFeatureData = room.getShiftedFeatureData();
			{
				// If the room overlaps the corridor, then abort.
				List<Coord> roomSpaces = roomFeatureData.stream().map(Pair::getFirst).collect(Collectors.toList());
				if (shiftedFeatureData.stream().anyMatch(pair -> roomSpaces.contains(pair.getFirst()))) {
					this.shiftedFeatureData = null;
					return;
				}
			}
			shiftedFeatureData.addAll(roomFeatureData);
			shiftedFeatureData.add(new Pair<>(new Coord(doorX, doorY), addDoor1 ? Terrain.WOOD_DOOR : Terrain.DUNGEON_FLOOR));
			this.shiftedFeatureData = shiftedFeatureData;
		}
		@Override public List<Pair<Coord, Terrain>> getShiftedFeatureData() {
			return shiftedFeatureData;
		}
		@Override public boolean needsBorders(Coord coord) {
			return !coord.equals(doorLoc);
		}
	}
	private static final class Cavern implements Feature {
		private final List<Pair<Coord, Terrain>> shiftedFeatureData;
		public Cavern(int frameWidth, int frameHeight, double rockPercentage, int iterations, Random random) {
			boolean[][] rock = new boolean[frameWidth][frameHeight];
			ArrayUtil.fill(rock, () -> random.nextDouble() < rockPercentage);
			
			for (int I=0; I<iterations; I++) {
				boolean[][] copy = ArrayUtil.copy(rock);
				IntStream.range(0, frameWidth).forEach(x -> IntStream.range(0, frameHeight).forEach(y -> {
					rock[x][y] = IntStream.rangeClosed(x-1, x+1).map(i -> IntStream.rangeClosed(y-1, y+1).map(j -> {
						try {
							return copy[i][j] ? 1 : 0;
						}
						catch (IndexOutOfBoundsException e) {
							return 1;
						}
					}).sum()).sum() >= 5;
				}));
			}
			List<List<Coord>> blobs = new ArrayList<>();
			boolean[][] visited = new boolean[frameWidth][frameHeight];
			for (int X=0; X<frameWidth; X++) {
				for (int Y=0; Y<frameHeight; Y++) {
					if (!rock[X][Y] && !visited[X][Y]) {
						List<Coord> blob = new ArrayList<>();
						Deque<Coord> stack = new ArrayDeque<>();
						stack.add(new Coord(X, Y));
						while (!stack.isEmpty()) {
							Coord currentLoc = stack.removeFirst();
							int x = currentLoc.x, y = currentLoc.y;
							try {
								if (visited[x][y]) {
									continue;
								}
							}
							catch (ArrayIndexOutOfBoundsException e) {
								continue;
							}
							visited[x][y] = true;
							if (rock[x][y]) continue;
							blob.add(currentLoc);
							for (Dir d : Dir.ORTHOGONAL_COMPASS) {
								stack.addLast(currentLoc.plus(d));
							}
						}
						blobs.add(blob);
					}
				}
			}
			blobs.sort((l1, l2) -> l1.size() - l2.size());
			List<Coord> chosenBlob = blobs.get(blobs.size()-1);
			
			this.shiftedFeatureData = chosenBlob.stream().map(coord -> new Pair<>(coord, Terrain.ROCK_FLOOR)).collect(Collectors.toList());
		}
		@Override public List<Pair<Coord, Terrain>> getShiftedFeatureData() {
			return shiftedFeatureData;
		}
		@Override public boolean needsBorders(Coord coord) {
			return false;
		}
	}
	
	public DungeonBuilder(int width, int height, int depth, int originalSeed) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.originalSeed = originalSeed;
	}
	
	@Override public MapTile[][] getMap() {
		Random random = new Random((originalSeed * (depth + Integer.MAX_VALUE / 2)) ^ depth);
		MapTile[][] map = new MapTile[width][height];
		
		// Fill dungeon with solid rock
		ArrayUtil.fill(map, () -> new MapTile(Terrain.ROCK_WALL));
		
		// Place first feature
		{
			Feature firstFeature;
			if (depth+1 >= 3 && random.nextInt(100) < 75) {
				firstFeature = new Cavern(width, height, 0.5, 5, random);
			}
			else {
				firstFeature = new Room(9, 7, width/2, height/2, Dir.ORTHOGONAL_COMPASS.get(random.nextInt(4)), random.nextInt(7));
			}
			if (firstFeature.canPlace(map, tile -> true)) {
				firstFeature.place(map);
			}
			else {
				throw new AssertionError();
			}
		}
		
		// Place more features branching off the first
		int attemptedRooms = 0;
		int successfulRooms = 0;
		while (attemptedRooms < 10000 && successfulRooms < 100) {
			// Pick two random adjacent cells, and check if they go across the wall of a room.
			int startX = random.nextInt(width),
					startY = random.nextInt(height);
			Dir direction = Dir.ORTHOGONAL_COMPASS.get(random.nextInt(4));
			int doorX = startX + direction.getX(),
					doorY = startY + direction.getY();
			if (doorX < 0 || doorY < 0 || doorX >= width || doorY >= height) continue;
			if ((map[startX][startY].getTerrain() == Terrain.ROCK_FLOOR || map[startX][startY].getTerrain() == Terrain.DUNGEON_FLOOR) &&
					(map[doorX][doorY].getTerrain() == Terrain.ROCK_WALL || map[doorX][doorY].getTerrain() == Terrain.DUNGEON_WALL)) {
				Feature feature;
				feature = new CorridorWithRoom(
						1 + random.nextInt(7),
						random.nextDouble(),
						7 + random.nextInt(7),
						4 + random.nextInt(4),
						doorX,
						doorY,
						direction,
						random);
				if (feature.canPlace(map, tile -> tile.getTerrain() == Terrain.ROCK_WALL || tile.getTerrain() == Terrain.DUNGEON_WALL)) {
					feature.place(map);
					successfulRooms += 1;
				}
				attemptedRooms += 1;
			}
		}
		
		// Set material of dungeon walls
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				Coord rootLoc = new Coord(x, y);
				if (map[x][y].getTerrain() == Terrain.ROCK_WALL &&
						Dir.ALL_COMPASS.stream().map(rootLoc::plus).anyMatch(coord -> {
					try {
						return map[coord.x][coord.y].getTerrain() == Terrain.DUNGEON_FLOOR;
					}
					catch (ArrayIndexOutOfBoundsException e) {
						return false;
					}
				})) {
					map[x][y].setTerrain(Terrain.DUNGEON_WALL);
				}
			}
		}
		
		// Place foliage
		Perlin2 grassNoise = new Perlin2(random.nextInt(), 4, 0.5, 1);
		double maxValue = grassNoise.getMaximumValue();
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				double noise = grassNoise.perlinNoise(x / 3.0, y / 3.0);
				if (noise >= maxValue * map[x][y].getTerrain().admitsThickGrass(depth)) {
					map[x][y].setTerrain(map[x][y].getTerrain().getThickGrass());
				}
				else if (noise >= maxValue * map[x][y].getTerrain().admitsThinGrass(depth)) {
					map[x][y].setTerrain(map[x][y].getTerrain().getThinGrass());
				}
				else if (noise >= maxValue * map[x][y].getTerrain().admitsFungus(depth)) {
					map[x][y].setTerrain(map[x][y].getTerrain().getMushrooms());
				}
			}
		}
		
		//                 X.
		// Remove corners: .X
		boolean foundOne;
		do {
			foundOne = false;
			for (int x=0; x<width-1; x++) {
				for (int y=0; y<height-1; y++) {
					if (map[x][y].getTerrain().canWalkOver() && map[x+1][y+1].getTerrain().canWalkOver() &&
							map[x+1][y].getTerrain().blocksMovement() && map[x][y+1].getTerrain().blocksMovement()) {
						map[x+1][y].setTerrain(Terrain.RUBBLE);
						map[x][y+1].setTerrain(Terrain.RUBBLE);
						foundOne = true;
					}
					else if (map[x+1][y].getTerrain().canWalkOver() && map[x][y+1].getTerrain().canWalkOver() &&
							map[x][y].getTerrain().blocksMovement() && map[x+1][y+1].getTerrain().blocksMovement()) {
						map[x][y].setTerrain(Terrain.RUBBLE);
						map[x+1][y+1].setTerrain(Terrain.RUBBLE);
						foundOne = true;
					}
				}
			}
		}
		while (foundOne);
		
		//                      ######
		// Remove stupid doors: ......
		//                      ..+###
		//                      ..####
		//                      ..####
		for (int x=1; x<width-1; x++) {
			for (int y=1; y<height-1; y++) {
				if (map[x][y].getTerrain() == Terrain.WOOD_DOOR) {
					if (!(
							map[x-1][y].getTerrain().blocksMovement() && map[x+1][y].getTerrain().blocksMovement() &&
							map[x][y-1].getTerrain().canWalkOver() && map[x][y+1].getTerrain().canWalkOver() ||
							map[x-1][y].getTerrain().canWalkOver() && map[x+1][y].getTerrain().canWalkOver() &&
							map[x][y-1].getTerrain().blocksMovement() && map[x][y+1].getTerrain().blocksMovement()
							)) {
						map[x][y].setTerrain(Terrain.RUBBLE);
					}
				}
				if (map[x][y].getTerrain().canWalkOver() && map[x+1][y+1].getTerrain().canWalkOver() &&
						map[x+1][y].getTerrain().blocksMovement() && map[x][y+1].getTerrain().blocksMovement()) {
					map[x+1][y].setTerrain(Terrain.RUBBLE);
					map[x][y+1].setTerrain(Terrain.RUBBLE);
					foundOne = true;
				}
				else if (map[x+1][y].getTerrain().canWalkOver() && map[x][y+1].getTerrain().canWalkOver() &&
						map[x][y].getTerrain().blocksMovement() && map[x+1][y+1].getTerrain().blocksMovement()) {
					map[x][y].setTerrain(Terrain.RUBBLE);
					map[x+1][y+1].setTerrain(Terrain.RUBBLE);
					foundOne = true;
				}
			}
		}
		
		// Place armor and weapons
		int numberOfItems = 1+random.nextInt(3);
		for (int i=0; i<numberOfItems; i++) {
			Coord itemLoc = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain().canWalkOver() && !tile.getTerrain().isStaircase() && tile.getTerrain() != Terrain.WOOD_DOOR && tile.getItem() == null, random);
			map[itemLoc.x][itemLoc.y].setItem(random.nextBoolean() ? Weapons.getAppropriate(depth, random).make() : Armors.getAppropriate(depth, random).make());
		}
		// Place food
		int amtFood = 1 + random.nextInt(2);
		for (int i=0; i<amtFood; i++) {
			Coord itemLoc = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain().canWalkOver() && !tile.getTerrain().isStaircase() && tile.getTerrain() != Terrain.WOOD_DOOR && tile.getItem() == null, random);
			map[itemLoc.x][itemLoc.y].setItem(Foods.getAppropriate(random).make());
		}
		
		// Place monsters
		int numberOfMonsters = 5+random.nextInt(5);
		for (int i=0; i<numberOfMonsters; i++) {
			Coord monsterLoc = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain().canWalkOver() && tile.getTerrain() != Terrain.WOOD_DOOR && tile.getEntity() == null, random);
			map[monsterLoc.x][monsterLoc.y].setEntity(Entities.getAppropriate(depth, 12 + depth * 2, random).make());
		}
		
		// Place staircases
		{
			Coord upStaircaseLoc = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain().admitsStaircase(), random);
			MapTile tile = map[upStaircaseLoc.x][upStaircaseLoc.y];
			tile.setTerrain(tile.getTerrain().getStaircase(false));
		}
		if (depth+1 != 26) {
			Coord downStaircaseLoc = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain().admitsStaircase(), random);
			MapTile tile = map[downStaircaseLoc.x][downStaircaseLoc.y];
			tile.setTerrain(tile.getTerrain().getStaircase(true));
		}
		else {
			// On the last level, plant a sleigh.
			Coord sleighLoc = MapAnalysis.getRandomTile(map, tile -> tile.getTerrain().canWalkOver() && !tile.getTerrain().isStaircase() && tile.getTerrain() != Terrain.WOOD_DOOR && tile.getItem() == null, random);
			map[sleighLoc.x][sleighLoc.y].setItem(new Sleigh());
		}
		
		return map;
	}
	
	@SuppressWarnings("unused")
	private void fill(MapTile[][] map, int minX, int maxX, int minY, int maxY, Terrain terrain) {
		for (int x=minX; x<=maxX; x++) {
			for (int y=minY; y<=maxY; y++) {
				map[x][y].setTerrain(terrain);
			}
		}
	}
	
	@Override public int getWidth() {
		return width;
	}
	@Override public int getHeight() {
		return height;
	}
	
}
