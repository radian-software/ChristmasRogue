package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.xmasrogue.ChristmasRogue;
import com.apprisingsoftware.xmasrogue.entity.Attack;
import com.apprisingsoftware.xmasrogue.entity.Entities;
import com.apprisingsoftware.xmasrogue.entity.Entity;
import com.apprisingsoftware.xmasrogue.entity.Inventory;
import com.apprisingsoftware.xmasrogue.entity.Item;
import com.apprisingsoftware.xmasrogue.util.Coord;
import com.apprisingsoftware.xmasrogue.util.Dir;
import com.apprisingsoftware.xmasrogue.util.ShadowCaster;
import com.apprisingsoftware.xmasrogue.world.MapAnalysis;
import com.apprisingsoftware.xmasrogue.world.MapBuilder;
import com.apprisingsoftware.xmasrogue.world.MapTile;
import com.apprisingsoftware.xmasrogue.world.Terrain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class MapScreen implements AsciiScreen {
	
	protected final int width, height;
	private final MapTile[][] tiles;
	private Set<Coord> updatedTiles;
	
	private Coord playerLocation;
	private List<Coord> entityLocations;
	private Coord upStaircase, downStaircase;
	
	public MapScreen(MapBuilder builder) {
		if (builder == null) throw new IllegalArgumentException();
		this.tiles = builder.getMap();
		this.width = tiles.length;
		this.height = tiles[0].length;
		this.updatedTiles = new HashSet<>();
		this.entityLocations = new ArrayList<>();
		
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				updatedTiles.add(new Coord(x, y));
				if (tiles[x][y].getEntity() != null) {
					if (tiles[x][y].getEntity().getType() == Entities.PLAYER) {
						playerLocation = new Coord(x, y);
					}
					else {
						entityLocations.add(new Coord(x, y));
					}
				}
				if (tiles[x][y].getTerrain().isUpstairs()) {
					upStaircase = new Coord(x, y);
				}
				if (tiles[x][y].getTerrain().isDownstairs()) {
					downStaircase = new Coord(x, y);
				}
			}
		}
	}
	
	public MapTile[][] getRawTileData() {
		return tiles;
	}
	public String getPlayerLocationDetail() {
		return tiles[playerLocation.x][playerLocation.y].getTerrain().getDescription();
	}
	
	public Result tryMovePlayer(Dir direction, Random gameRandom, Inventory inventory) {
		if (playerLocation == null) throw new IllegalStateException();
		Entity player = tiles[playerLocation.x][playerLocation.y].getEntity();
		if (player.getType() != Entities.PLAYER) throw new AssertionError();
		Coord newLoc = playerLocation.plus(direction);
		if (newLoc.x < 0 || newLoc.y < 0 || newLoc.x >= width || newLoc.y >= height) {
			return Result.from(new Message("A strange force prevents you from walking in this direction.", Message.FAILURE_COLOR)).withoutWorldTick();
		}
		Terrain newTile = tiles[newLoc.x][newLoc.y].getTerrain();
		if (newTile.blocksMovement()) {
			return Result.from(new Message(newTile.getDescription(), Message.FAILURE_COLOR)).withoutWorldTick();
		}
		if (direction.isDiagonal()) {
			List<Coord> diagonalLocs = Arrays.asList(playerLocation.plus(direction.toX()), playerLocation.plus(direction.toY()));
			if (diagonalLocs.stream().allMatch(loc -> tiles[loc.x][loc.y].getTerrain().blocksMovement())) {
				return Result.from(new Message("You can't quite squeeze through this gap.", Message.FAILURE_COLOR)).withoutWorldTick();
			}
		}
		Entity enemy = tiles[newLoc.x][newLoc.y].getEntity();
		if (enemy != null) {
			Attack attack = player.getAttack(gameRandom, inventory);
			List<Message> messages = new ArrayList<>();
			if (enemy.getState() == Entity.State.TRACKING) {
				messages.add(new Message(String.format("You %s the %s!", attack.getVerb(), enemy.getName()), Message.MONSTER_DAMAGE_COLOR));
				enemy.damage(attack.getDamage());
			}
			else {
				messages.add(new Message(String.format("You %s the %s in its sleep!", attack.getVerb(), enemy.getName()), Message.MONSTER_DAMAGE_COLOR));
				enemy.damage(attack.getDamage() * 3);
				enemy.setState(Entity.State.TRACKING);
			}
			if (enemy.isDead()) {
				messages.add(new Message(String.format("You have killed the %s.", enemy.getName()), Message.MONSTER_DAMAGE_COLOR));
				player.increaseMaxHP(enemy.getMaxHP() * Entity.healthAbsorptionFactor);
				tiles[newLoc.x][newLoc.y].removeEntity();
				entityLocations.remove(newLoc);
				updatedTiles.add(newLoc);
			}
			return Result.from(messages).withWorldTick();
		}
		// Otherwise, move the player.
		tiles[playerLocation.x][playerLocation.y].removeEntity();
		tiles[newLoc.x][newLoc.y].setEntity(player);
		// Make the movement visible,
		updatedTiles.add(playerLocation);
		updatedTiles.add(newLoc);
		// Catalog it,
		playerLocation = newLoc;
		// Slow down regeneration,
		player.justMoved();
		// Pick up an item here if possible
		Item item = tiles[newLoc.x][newLoc.y].getItem();
		if (item != null) {
			if (inventory.addItem(item)) {
				tiles[newLoc.x][newLoc.y].removeItem();
				return Result.WORLD_TICK.with(new Message(String.format("You pick up the %s.", item.getName()), Message.MENU_COLOR));
			}
			else {
				return Result.WORLD_TICK.with(new Message(String.format("You do not have enough room in your inventory to pick up a %s.", item.getName()), Message.MENU_COLOR));
			}
		}
		else {
			return Result.WORLD_TICK;
		}
	}
	public Result tryDropItem(Inventory inventory, char id) {
		Item item = inventory.removeItem(id);
		if (item != null) {
			Item existingItem = tiles[playerLocation.x][playerLocation.y].getItem();
			if (existingItem == null) {
				tiles[playerLocation.x][playerLocation.y].setItem(item);
				return Result.WORLD_TICK.withSuccess().with(new Message(String.format("You drop the %s.", item.getName()), Message.MENU_COLOR));
			}
			else {
				if (!inventory.addItem(existingItem)) {
					throw new AssertionError();
				}
				return Result.WORLD_TICK.withSuccess().with(new Message(String.format("You drop the %s and pick up the %s back up.", item.getName(), existingItem.getName()), Message.MENU_COLOR));
			}
		}
		else {
			return Result.NO_WORLD_TICK.withoutSuccess();
		}
	}
	public Result transferEntities(Collection<Entity> transferredEntities, Random random) {
		for (Entity entity : transferredEntities) {
			switch (entity.getPlacement()) {
			case RANDOM:
				boolean spaceAvailable = false;
				checkingIfSpaceIsAvailable:
					for (int x=0; x<width; x++) {
						for (int y=0; y<height; y++) {
							if (tiles[x][y].getTerrain().isOpen() && tiles[x][y].getEntity() == null) {
								spaceAvailable = true;
								break checkingIfSpaceIsAvailable;
							}
						}
					}
				if (spaceAvailable) {
					int x, y;
					do {
						x = random.nextInt(width);
						y = random.nextInt(height);
					}
					while (tiles[x][y].getTerrain().isSolid() || tiles[x][y].getEntity() != null);
					tiles[x][y].setEntity(entity);
					updatedTiles.add(new Coord(x, y));
					if (entity.getType() == Entities.PLAYER) {
						playerLocation = new Coord(x, y);
					}
				}
				else {
					throw new IllegalStateException();
				}
				break;
			case UP_STAIRCASE:
			case DOWN_STAIRCASE:
				Coord targetLocation = entity.getPlacement() == Entity.Placement.UP_STAIRCASE ? upStaircase : downStaircase;
				if (targetLocation == null) throw new IllegalStateException();
				boolean success = false;
				// Highly inefficient on a large scale, but 99% of the time only the d = 0 case will be executed.
				for (int d=0; d<Math.max(width, height); d++) {
					List<Coord> possibilities = new ArrayList<>();
					for (int x=targetLocation.x-d; x<=targetLocation.x+d; x++) {
						for (int y=targetLocation.y-d; y<=targetLocation.y+d; y++) {
							if (x != targetLocation.x-d && x != targetLocation.x+d && y != targetLocation.y-d && y != targetLocation.y+d)
								continue;
							if (tiles[x][y].getTerrain().isOpen() && tiles[x][y].getEntity() == null) {
								possibilities.add(new Coord(x, y));
							}
						}
					}
					if (!possibilities.isEmpty()) {
						Coord loc = possibilities.get(random.nextInt(possibilities.size()));
						tiles[loc.x][loc.y].setEntity(entity);
						updatedTiles.add(loc);
						if (entity.getType() == Entities.PLAYER) {
							playerLocation = new Coord(loc.x, loc.y);
						}
						success = true;
						break;
					}
				}
				if (!success) throw new IllegalStateException();
				break;
			default: throw new AssertionError();
			}
		}
		return Result.NONE;
	}
	public Result tryUseStaircase(boolean goingDown, boolean tryingToEscape, boolean canEscape) {
		int x = playerLocation.x, y = playerLocation.y;
		boolean upstairsPresent = tiles[x][y].getTerrain().isUpstairs(),
				downstairsPresent = tiles[x][y].getTerrain().isDownstairs();
		if (ChristmasRogue.DEBUG() || (!goingDown && upstairsPresent || goingDown && downstairsPresent)) { // i.e., going up the up staircase or going down the down staircase
			if (tryingToEscape) {
				if (canEscape) {
					tiles[x][y].removeEntity();
					playerLocation = null;
					return Result.SUCCESS.withoutWorldTick().with(new Message("You ascend triumphantly into the sunlight...", Message.MONSTER_DAMAGE_COLOR));
				}
				else {
					return Result.FAILURE.withoutWorldTick().with(
							new Message("You can't leave without getting your sleigh back!", Message.FAILURE_COLOR));
				}
			}
			// The player will be moved to the next floor, ergo s/he is not on this floor
			tiles[x][y].removeEntity();
			playerLocation = null;
			return Result.SUCCESS.withoutWorldTick();
		}
		else {
			return Result.FAILURE.with(new Message(
					goingDown ? "There is no down staircase here." : "There is no up staircase here.", Message.FAILURE_COLOR)).withoutWorldTick();
		}
	}
	public Result tickWorld(Random gameRandom, Inventory inventory) {
		List<Message> messages = new ArrayList<>();
		
		{
			Entity player = tiles[playerLocation.x][playerLocation.y].getEntity();
			if (player == null || player.getType() != Entities.PLAYER) throw new AssertionError();
			player.tick();
		}
		
		for (Coord entityLocation : entityLocations) {
			Entity entity = tiles[entityLocation.x][entityLocation.y].getEntity();
			if (entity == null || entity.getType() == Entities.PLAYER) throw new AssertionError();
			if (entity.getState() == Entity.State.SLEEPING) continue;
			Dir moveDir = MapAnalysis.pathfind(tiles, entityLocation, playerLocation, tile -> tile.getTerrain().canWalkOver() && tile.getEntity() == null, gameRandom);
			if (moveDir == null) {
				// Prefer to find a path around other entities, but if this is not possible (e.g. a monster is blocking a corridor)
				// try anyway.
				moveDir = MapAnalysis.pathfind(tiles, entityLocation, playerLocation, tile -> tile.getTerrain().canWalkOver(), gameRandom);
			}
			Coord newLoc = entityLocation.plus(moveDir);
			if (newLoc.x < 0 || newLoc.y < 0 || newLoc.x >= width || newLoc.y >= height) {
				throw new AssertionError();
			}
			Terrain newTile = tiles[newLoc.x][newLoc.y].getTerrain();
			if (newTile.blocksMovement()) {
				throw new AssertionError();
			}
			Entity player = tiles[newLoc.x][newLoc.y].getEntity();
			if (player != null) {
				if (player.getType() == Entities.PLAYER) {
					Attack attack = entity.getAttack(gameRandom);
					messages.add(new Message(
							String.format("The %s %s you!", entity.getName(), attack.getVerb()), Message.PLAYER_DAMAGE_COLOR));
					player.damage(attack.getDamage(), inventory);
					if (player.isDead()) {
						messages.add(new Message(
								"You die...", Message.FAILURE_COLOR));
						return Result.PLAYER_IS_DEAD.with(messages);
					}
					continue;
				}
				else {
					continue;
				}
			}
			tiles[entityLocation.x][entityLocation.y].removeEntity();
			tiles[newLoc.x][newLoc.y].setEntity(entity);
			updatedTiles.add(entityLocation);
			updatedTiles.add(newLoc);
			entityLocations.set(entityLocations.indexOf(entityLocation), newLoc);
			entity.justMoved();
		}
		return Result.PLAYER_IS_ALIVE.with(messages);
	}
	public void updateLightMap(int depth, Random random, Inventory inventory) {
		if (ChristmasRogue.DEBUG()) {
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					if (tiles[x][y].getShading() != MapTile.Shading.VISIBLE) {
						tiles[x][y].setShading(MapTile.Shading.VISIBLE);
					}
				}
			}
		}
		else {
			float[][] resistanceMap = new float[width][height];
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					if (tiles[x][y].getTerrain() == Terrain.WOOD_DOOR) {
						resistanceMap[x][y] = 1.0f;
					}
					else if (tiles[x][y].getTerrain().canWalkOver()) {
						resistanceMap[x][y] = 0.0f;
					}
					else {
						resistanceMap[x][y] = 1.0f;
					}
				}
			}
			double radius = 1 + 100 / (depth + 5.0);
			float[][] lightMap = ShadowCaster.calculateFOV(resistanceMap, playerLocation.x, playerLocation.y, radius, 1.0f);
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					if (lightMap[x][y] > 0.0f) {
						if (tiles[x][y].getShading() != MapTile.Shading.VISIBLE) {
							updatedTiles.add(new Coord(x, y));
						}
						tiles[x][y].setShading(MapTile.Shading.VISIBLE);
						// Wake up monsters if applicable
						Entity entity = tiles[x][y].getEntity();
						if (entity != null && entity.getType() != Entities.PLAYER && entity.getState() == Entity.State.SLEEPING) {
							double distance = Math.sqrt(Math.pow(x - playerLocation.x, 2) + Math.pow(y - playerLocation.y, 2));
							double closeness = 1 - distance / radius; // close to 1 is bad
							double armorRating = inventory.getCurrentArmor() == null ? 0 : 1 - inventory.getCurrentArmor().reduceDamage(1); // close to 1 is bad
							double wakeChance = (closeness - 0.2) * (armorRating + 0.05);
							if (random.nextDouble() < wakeChance) {
								entity.setState(Entity.State.TRACKING);
							}
						}
					}
					else {
						if (tiles[x][y].getShading() != MapTile.Shading.UNDISCOVERED) {
							if (tiles[x][y].getShading() != MapTile.Shading.DISCOVERED) {
								updatedTiles.add(new Coord(x, y));
							}
							tiles[x][y].setShading(MapTile.Shading.DISCOVERED);
						}
					}
				}
			}
		}
	}
	
	@Override public AsciiTile getTile(int x, int y) {
		return tiles[x][y].getAsciiTile();
	}
	@Override public boolean isTransparent(int x, int y) {
		return !inBounds(x, y);
	}
	
	@Override public Collection<Coord> getUpdatedTiles() {
		Set<Coord> copy = updatedTiles;
		updatedTiles = new HashSet<>();
		return copy;
	}
	
	@Override public int getWidth() {
		return width;
	}
	
	@Override public int getHeight() {
		return height;
	}
	
}
