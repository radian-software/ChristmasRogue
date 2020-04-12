package com.apprisingsoftware.xmasrogue.world;

import com.apprisingsoftware.util.ArrayUtil;
import com.apprisingsoftware.xmasrogue.util.Coord;
import com.apprisingsoftware.xmasrogue.util.Dir;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public final class MapAnalysis {
	
	private MapAnalysis() {}
	
	public static Coord getRandomTile(MapTile[][] tiles, Predicate<MapTile> condition, Random random) {
		int width = tiles.length, height = tiles[0].length;
		boolean spaceAvailable = false;
		lookingForAvailableSpace: {
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					if (condition.test(tiles[x][y])) {
						spaceAvailable = true;
						break lookingForAvailableSpace;
					}
				}
			}
		}
		int x, y;
		if (!spaceAvailable) return null;
		else do {
			x = random.nextInt(width);
			y = random.nextInt(height);
		}
		while (!condition.test(tiles[x][y]));
		return new Coord(x, y);
	}
	
	public static Dir pathfind(MapTile[][] map, Coord start, Coord goal, Predicate<MapTile> crossable, Random random) {
		int width = map.length, height = map[0].length;
		int[][] distanceMap = new int[width][height];
		ArrayUtil.fill(distanceMap, Integer.MAX_VALUE);
		int shortestPathLength = Integer.MAX_VALUE;
		Deque<Coord> queue = new ArrayDeque<>();
		queue.add(goal);
		distanceMap[goal.x][goal.y] = 0;
		while (!queue.isEmpty()) {
			Coord loc = queue.removeFirst();
			if (loc.equals(start)) {
				shortestPathLength = Math.min(shortestPathLength, distanceMap[start.x][start.y]);
			}
			if (distanceMap[loc.x][loc.y] >= shortestPathLength) continue;
			List<Coord> neighbors = Dir.getAllNeighbors(loc);
			for (Coord neighbor : neighbors) try {
				if (distanceMap[neighbor.x][neighbor.y] > distanceMap[loc.x][loc.y] + 1 && crossable.test(map[neighbor.x][neighbor.y])) {
					distanceMap[neighbor.x][neighbor.y] = distanceMap[loc.x][loc.y] + 1;
					queue.addLast(neighbor);
				}
			} catch (IndexOutOfBoundsException e) {}
		}
		List<Dir> bestDirections = new ArrayList<>();
		int shortestDirectionPath = Integer.MAX_VALUE;
		for (Dir Dir : Dir.ALL_COMPASS) try {
			Coord neighbor = start.plus(Dir);
			if (distanceMap[neighbor.x][neighbor.y] <= shortestDirectionPath) {
				if (distanceMap[neighbor.x][neighbor.y] < shortestDirectionPath) {
					bestDirections = new ArrayList<>();
				}
				shortestDirectionPath = distanceMap[neighbor.x][neighbor.y];
				bestDirections.add(Dir);
			}
		} catch (IndexOutOfBoundsException e) {}
		if (shortestDirectionPath == Integer.MAX_VALUE) return null;
		else return bestDirections.get(random.nextInt(bestDirections.size()));
	}
	
}
