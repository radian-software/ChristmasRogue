package com.apprisingsoftware.xmasrogue.io;

import asciiPanel.AsciiPanel;
import com.apprisingsoftware.util.Pair;
import com.apprisingsoftware.xmasrogue.entity.Entities;
import com.apprisingsoftware.xmasrogue.entity.Entity;
import com.apprisingsoftware.xmasrogue.entity.Inventory;
import com.apprisingsoftware.xmasrogue.util.Coord;
import com.apprisingsoftware.xmasrogue.util.Dir;
import com.apprisingsoftware.xmasrogue.util.Rect;
import com.apprisingsoftware.xmasrogue.world.DungeonBuilder;
import com.apprisingsoftware.xmasrogue.world.MapLoader;
import com.apprisingsoftware.xmasrogue.world.MapTile;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class GameAsciiScreen extends CascadedAsciiScreen implements NestedInputAcceptingAsciiScreen {
	
	private enum State {
		GAMEPLAY,
		PLAYER_HAS_DIED,
		INVENTORY,
		DROPPING_ITEM,
		EQUIPPING_ITEM,
		REMOVING_ITEM,
	}
	
	private List<MapTile[][]> dungeon;
	private Entity player;
	private Inventory inventory;
	private final int originalSeed;
	private Random gameRandom;
	private int currentLevel;
	
	private MapScreen mapScreen;
	private MessageScreen messageScreen;
	private ProgressBarScreen healthBar;
	private ProgressBarScreen satiationBar;
	private LabelScreen locationLabel;
	private LabelScreen levelLabel;
	
	private final Rect mapScreenRect;
	private Deque<List<Message>> overflowMessages;
	private State currentState;
	
	private static final int leftColumnWidth = 16;
	
	private GameAsciiScreen(int width, int height, int originalSeed, @SuppressWarnings("unused") Object dummy) {
		super(width, height);
		if (width <= 0 || height <= 0) throw new IllegalArgumentException();
		
		this.mapScreenRect = new Rect(leftColumnWidth, 3, width - leftColumnWidth, height - 4);
		this.originalSeed = originalSeed;
		currentState = State.GAMEPLAY;
	}
	public GameAsciiScreen(int width, int height, int originalSeed) {
		this(width, height, originalSeed, null);
		// Randomness
		this.gameRandom = new Random(originalSeed);
		// Current level
		this.currentLevel = 0;
		setMapScreen(new MapScreen(new DungeonBuilder(mapScreenRect.getWidth(), mapScreenRect.getHeight(), this.currentLevel, originalSeed)));
		// Dungeon
		this.dungeon = new ArrayList<>();
		this.dungeon.add(this.mapScreen.getRawTileData());
		// Player
		this.player = Entities.PLAYER.make();
		this.player.setPlacement(Entity.Placement.UP_STAIRCASE);
		this.inventory = new Inventory();
		this.mapScreen.transferEntities(Arrays.asList(this.player), this.gameRandom);
		// HUD
		createHUD();
	}
	public GameAsciiScreen(int width, int height, int originalSeed, Inventory inventory, List<MapTile[][]> dungeon, Random gameRandom) {
		this(width, height, originalSeed, null);
		if (dungeon == null || gameRandom == null) throw new IllegalArgumentException();
		// Randomness
		this.gameRandom = gameRandom;
		// Dungeon
		this.dungeon = dungeon;
		// Current level; Player
		this.currentLevel = -1;
		findingCurrentLevel:
			for (MapTile[][] level : this.dungeon) {
				for (int x=0; x<width; x++) {
					for (int y=0; y<height; y++) {
						if (level[x][y].getEntity() != null && level[x][y].getEntity().getType() == Entities.PLAYER) {
							this.currentLevel = this.dungeon.indexOf(level);
							this.player = level[x][y].getEntity();
							this.inventory = inventory;
							break findingCurrentLevel;
						}
					}
				}
			}
		if (this.currentLevel == -1 || this.player == null) throw new AssertionError();
		setMapScreen(new MapScreen(new MapLoader(this.dungeon.get(this.currentLevel))));
		
		createHUD();
	}
	
	private void setMapScreen(MapScreen screen) {
		this.mapScreen = screen;
		if (depth() == 0) {
			addScreen(screen, mapScreenRect.getLeft(), mapScreenRect.getTop());
		}
		else {
			setScreen(0, screen, mapScreenRect.getLeft(), mapScreenRect.getTop());
		}
	}
	private void createHUD() {
		Rect messageLogRect = new Rect(leftColumnWidth, 0, width - leftColumnWidth, 3);
		Rect healthBarRect = new Rect(0, 0, leftColumnWidth, 1);
		Rect satiationBarRect = new Rect(0, 1, leftColumnWidth, 1);
		Rect locationTextRect = new Rect(leftColumnWidth + 1, height - 1, width - leftColumnWidth - 1, 1);
		Rect levelLabelRect = new Rect(0, height - 1, leftColumnWidth, 1);
		
		this.messageScreen = new MessageScreen(messageLogRect.getWidth(), messageLogRect.getHeight(), AsciiPanel.black);
		this.healthBar = new ProgressBarScreen(healthBarRect.getWidth(), AsciiPanel.blue, AsciiPanel.black, () -> player.getHPFraction(), "Health", AsciiPanel.white);
		this.satiationBar = new ProgressBarScreen(satiationBarRect.getWidth(), AsciiPanel.green, AsciiPanel.black, () -> player.getSatiationFraction(), "Hunger", AsciiPanel.white);
		this.locationLabel = new LabelScreen(locationTextRect.getWidth(), AsciiPanel.black, LabelScreen.Alignment.LEFT);
		this.levelLabel = new LabelScreen(levelLabelRect.getWidth(), AsciiPanel.brightBlack, LabelScreen.Alignment.LEFT);
		addOrSetScreens(Arrays.asList(
				new Pair<>(messageScreen, new Coord(messageLogRect.getLeft(), messageLogRect.getTop())),
				new Pair<>(healthBar, new Coord(healthBarRect.getLeft(), healthBarRect.getTop())),
				new Pair<>(satiationBar, new Coord(satiationBarRect.getLeft(), satiationBarRect.getTop())),
				new Pair<>(locationLabel, new Coord(locationTextRect.getLeft(), locationTextRect.getTop())),
				new Pair<>(levelLabel, new Coord(levelLabelRect.getLeft(), levelLabelRect.getTop()))
				), 1);
		this.overflowMessages = new ArrayDeque<>();
		updateLocationBar();
		updateLevelLabel();
		mapScreen.updateLightMap(currentLevel, gameRandom, inventory);
	}
	
	@Override public NestedInputAcceptingAsciiScreen respondToInput(KeyEvent e) {
		// You can quit from anywhere.
		if (e.getKeyCode() == KeyEvent.VK_Q && e.isShiftDown()) {
			return new MainMenuScreen(width, height);
		}
		// All the messages shall be shown before you can take any action. Except quitting.
		if (!overflowMessages.isEmpty()) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				showNextMessageSet();
				if (!overflowMessages.isEmpty()) {
					return null;
				}
			}
			else {
				return null;
			}
		}
		switch (currentState) {
		case GAMEPLAY:
			Result playerResult = Result.NO_WORLD_TICK;
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_4:
			case KeyEvent.VK_H:
				playerResult = mapScreen.tryMovePlayer(Dir.LEFT, gameRandom, inventory);
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_6:
			case KeyEvent.VK_L:
				playerResult = mapScreen.tryMovePlayer(Dir.RIGHT, gameRandom, inventory);
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_8:
			case KeyEvent.VK_K:
				playerResult = mapScreen.tryMovePlayer(Dir.UP, gameRandom, inventory);
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_2:
			case KeyEvent.VK_J:
				playerResult = mapScreen.tryMovePlayer(Dir.DOWN, gameRandom, inventory);
				break;
			case KeyEvent.VK_7:
			case KeyEvent.VK_Y:
				playerResult = mapScreen.tryMovePlayer(Dir.UPLEFT, gameRandom, inventory);
				break;
			case KeyEvent.VK_9:
			case KeyEvent.VK_U:
				playerResult = mapScreen.tryMovePlayer(Dir.UPRIGHT, gameRandom, inventory);
				break;
			case KeyEvent.VK_1:
			case KeyEvent.VK_B:
				playerResult = mapScreen.tryMovePlayer(Dir.DOWNLEFT, gameRandom, inventory);
				break;
			case KeyEvent.VK_3:
			case KeyEvent.VK_N:
				playerResult = mapScreen.tryMovePlayer(Dir.DOWNRIGHT, gameRandom, inventory);
				break;
			case KeyEvent.VK_PERIOD: {
				if (e.isShiftDown()) {
					playerResult = mapScreen.tryUseStaircase(true, false, inventory.containsSleigh());
					if (playerResult.wasSuccessful()) {
						changeLevel(true);
					}
				}
				else {
					playerResult = Result.WORLD_TICK;
				}
				break;
			}
			case KeyEvent.VK_Z:
				playerResult = Result.WORLD_TICK;
				break;
			case KeyEvent.VK_COMMA: {
				if (e.isShiftDown()) {
					playerResult = mapScreen.tryUseStaircase(false, currentLevel == 0, inventory.containsSleigh());
					if (playerResult.wasSuccessful()) {
						if (currentLevel == 0) {
							return new VictoryScreen(width, height);
						}
						else {
							changeLevel(false);
						}
					}
					break;
				}
			}
			case KeyEvent.VK_I:
				if (showInventory("Inventory")) {
					currentState = State.INVENTORY;
				}
				else {
					playerResult = Result.from(new Message("Your inventory is empty.", Message.FAILURE_COLOR)).withoutWorldTick();
				}
				break;
			case KeyEvent.VK_D:
				if (showInventory("Drop which item?")) {
					currentState = State.DROPPING_ITEM;
				}
				else {
					playerResult = Result.from(new Message("You have no items to drop.", Message.FAILURE_COLOR)).withoutWorldTick();
				}
				break;
			case KeyEvent.VK_E:
				if (showInventory("Use which item?")) {
					currentState = State.EQUIPPING_ITEM;
				}
				else {
					playerResult = Result.from(new Message("You have no items to equip.", Message.FAILURE_COLOR)).withoutWorldTick();
				}
				break;
			case KeyEvent.VK_R:
				if (showInventory("Remove which item?")) {
					currentState = State.REMOVING_ITEM;
				}
				else {
					playerResult = Result.from(new Message("You have no items to remove.", Message.FAILURE_COLOR)).withoutWorldTick();
				}
				break;
			}
			if (playerResult != null) {
				tickWorldAndShowMessages(playerResult);
			}
			return null;
		case PLAYER_HAS_DIED:
			return new MainMenuScreen(width, height);
		case INVENTORY:
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				currentState = State.GAMEPLAY;
				hideInventory();
				break;
			}
			return null;
		case DROPPING_ITEM:
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				currentState = State.GAMEPLAY;
				hideInventory();
			}
			else {
				char c = e.getKeyChar();
				if (c != KeyEvent.CHAR_UNDEFINED && c >= 'a' && c <= 'z') {
					Result result = mapScreen.tryDropItem(inventory, c);
					if (result.wasSuccessful()) {
						currentState = State.GAMEPLAY;
						hideInventory();
					}
					tickWorldAndShowMessages(result);
				}
			}
			return null;
		case EQUIPPING_ITEM:
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				currentState = State.GAMEPLAY;
				hideInventory();
			}
			else {
				char c = e.getKeyChar();
				if (c != KeyEvent.CHAR_UNDEFINED && c >= 'a' && c <= 'z') {
					Result result = inventory.apply(c, player);
					if (result.wasSuccessful()) {
						currentState = State.GAMEPLAY;
						hideInventory();
					}
					tickWorldAndShowMessages(result);
				}
			}
			return null;
		case REMOVING_ITEM:
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				currentState = State.GAMEPLAY;
				hideInventory();
			}
			else {
				char c = e.getKeyChar();
				if (c != KeyEvent.CHAR_UNDEFINED && c >= 'a' && c <= 'z') {
					Result result = inventory.unequip(c);
					if (result.wasSuccessful()) {
						currentState = State.GAMEPLAY;
						hideInventory();
					}
					tickWorldAndShowMessages(result);
				}
			}
			return null;
		default: throw new AssertionError();
		}
	}
	private void tickWorldAndShowMessages(Result result) {
		messageScreen.dilapidateOldMessages();
		Result worldResult = Result.PLAYER_IS_ALIVE;
		if (result.tickWorld()) {
			worldResult = mapScreen.tickWorld(gameRandom, inventory);
		}
		addMessages(result.with(worldResult.getMessages()).getMessages());
		if (worldResult.playerHasDied()) {
			currentState = State.PLAYER_HAS_DIED;
			// Pause before cutting back to main menu.
			overflowMessages.add(Collections.emptyList());
		}
		if (!overflowMessages.isEmpty()) {
			showNextMessageSet();
		}
		updateLocationBar();
		if (result.tickWorld()) {
			mapScreen.updateLightMap(currentLevel, gameRandom, inventory);
		}
	}
	private void addMessages(List<Message> messages) {
		Iterator<Message> it = messages.iterator();
		List<Message> messageSet = new ArrayList<>();
		while (it.hasNext()) {
			if (messageSet.size() == messageScreen.getNumMessages()) {
				overflowMessages.add(messageSet);
				messageSet = new ArrayList<>();
			}
			messageSet.add(it.next());
		}
		overflowMessages.add(messageSet);
	}
	private void showNextMessageSet() {
		List<Message> messageSet = overflowMessages.removeFirst();
		for (Message message : messageSet) {
			messageScreen.pushMessage(message);
		}
		if (!overflowMessages.isEmpty()) {
			messageScreen.showMorePrompt();
		}
		else {
			messageScreen.hideMorePrompt();
		}
	}
	private void updateLocationBar() {
		locationLabel.postMessage(new Message(String.format("You are standing on %s.", mapScreen.getPlayerLocationDetail()), Message.STATUS_COLOR));
	}
	private void changeLevel(boolean goingDown) {
		int newLevel = currentLevel + (goingDown ? 1 : -1);
		if (newLevel < dungeon.size()) {
			currentLevel = newLevel;
			setMapScreen(new MapScreen(new MapLoader(dungeon.get(currentLevel))));
			player.setPlacement(goingDown ? Entity.Placement.UP_STAIRCASE : Entity.Placement.DOWN_STAIRCASE);
			mapScreen.transferEntities(Arrays.asList(player), gameRandom);
		}
		else if (newLevel == dungeon.size()) {
			currentLevel = newLevel;
			setMapScreen(new MapScreen(new DungeonBuilder(mapScreen.getWidth(), mapScreen.getHeight(), currentLevel, originalSeed)));
			dungeon.add(mapScreen.getRawTileData());
			player.setPlacement(goingDown ? Entity.Placement.UP_STAIRCASE : Entity.Placement.DOWN_STAIRCASE);
			mapScreen.transferEntities(Arrays.asList(player), gameRandom);
		}
		else throw new AssertionError();
		updateLevelLabel();
		mapScreen.updateLightMap(currentLevel, gameRandom, inventory);
	}
	private void updateLevelLabel() {
		levelLabel.postMessage(new Message(String.format("   Depth: %d", currentLevel + 1), Message.STATUS_COLOR));
	}
	private boolean showInventory(String title) {
		InventoryScreen screen = new InventoryScreen(inventory, title);
		if (screen.isEmpty()) {
			return false;
		}
		else {
			Rect inventoryRect = new Rect(width - screen.getWidth() - 1, 3 + 1, screen.getWidth(), screen.getHeight());
			addScreen(screen, inventoryRect.getLeft(), inventoryRect.getTop());
			return true;
		}
	}
	private void hideInventory() {
		removeTopScreen();
	}
	
}
