package com.apprisingsoftware.xmasrogue.world;

import java.awt.Color;

final class C {
	public static final Color rockFloor = new Color(0x001a29);
	public static final Color stoneFloor = new Color(0x1a1a1a);
	public static final Color rockWall = new Color(0xdfc3c3);
	public static final Color stoneWall = new Color(0xd9c9c9);
	public static final Color woodDoor = new Color(0xDE6C2F);
	public static final Color mushrooms = new Color(0x6d4677);
}

public enum Terrain {
	
	//				isWall	char	fgColor					bgColor				description
	ROCK_WALL(		true,	'#',	Color.BLACK,			C.rockWall,			"The wall is a rough surface of granite."),
	DUNGEON_WALL(	true,	'#',	Color.BLACK,			C.stoneWall,		"The dungeon wall is smooth and solid."),
	
	ROCK_FLOOR(		false,	'.',	Color.WHITE,			C.rockFloor,		"a rough stone floor"),
	RUBBLE(			false,	',',	Color.WHITE,			C.rockFloor,		"scattered rubble and gravel"),
	DUNGEON_FLOOR(	false,	'.',	Color.WHITE,			C.stoneFloor,		"a roughly tiled stone floor"),
	
	THICK_GRASS_R(	false,	'"',	Color.GREEN,			C.rockFloor,		"a patch of thick grass"),
	THIN_GRASS_R(	false,	'\'',	Color.GREEN.brighter(),	C.rockFloor,		"a patch of sparse grass"),
	THICK_GRASS_S(	false,	'"',	Color.GREEN,			C.stoneFloor,		"a patch of thick grass"),
	THIN_GRASS_S(	false,	'\'',	Color.GREEN.brighter(),	C.stoneFloor,		"a patch of sparse grass"),
	MUSHROOMS_R(	false,	'\'',	C.mushrooms,			C.rockFloor,		"a patch of odd-looking mushrooms"),
	MUSHROOMS_S(	false,	'\'',	C.mushrooms,			C.stoneFloor,		"a patch of odd-looking mushrooms"),
	
	UPSTAIRS_R(		false,	'<',	Color.WHITE,			C.rockFloor,		"the bottom step of a staircase leading upwards"),
	DOWNSTAIRS_R(	false,	'>',	Color.WHITE,			C.rockFloor,		"the top step of a staircase leading downwards"),
	UPSTAIRS_S(		false,	'<',	Color.WHITE,			C.stoneFloor,		"the bottom step of a staircase leading upwards"),
	DOWNSTAIRS_S(	false,	'>',	Color.WHITE,			C.stoneFloor,		"the top step of a staircase leading downwards"),
	
	WOOD_DOOR(		false,	'+',	Color.BLACK,			C.woodDoor,			"the threshold of a door"),
	;
	
	private final boolean isWall;
	private final char character;
	private final Color fgColor, bgColor;
	private final String description;
	
	private Terrain(boolean isWall, char character, Color fgColor, Color bgColor, String description) {
		this.isWall = isWall;
		this.character = character;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.description = description;
	}
	
	public boolean isWall() {
		return isWall;
	}
	public boolean isSolid() {
		return isWall;
	}
	public boolean canWalkOver() {
		return !isWall;
	}
	public boolean blocksMovement() {
		return isWall;
	}
	public boolean isOpen() {
		return !isWall;
	}
	public boolean isUpstairs() {
		return this == Terrain.UPSTAIRS_R || this == Terrain.UPSTAIRS_S;
	}
	public boolean isDownstairs() {
		return this == Terrain.DOWNSTAIRS_R || this == Terrain.DOWNSTAIRS_S;
	}
	public boolean isStaircase() {
		return isUpstairs() || isDownstairs();
	}
	public boolean admitsStaircase() {
		return this == Terrain.ROCK_FLOOR || this == Terrain.DUNGEON_FLOOR || this.isGrass();
	}
	public Terrain getStaircase(boolean down) {
		if (getBackColor() == C.rockFloor) {
			return down ? DOWNSTAIRS_R : UPSTAIRS_R;
		}
		else if (getBackColor() == C.stoneFloor) {
			return down ? DOWNSTAIRS_S : UPSTAIRS_S;
		}
		else {
			throw new IllegalStateException();
		}
	}
	public double admitsThickGrass(int depth) {
		double factor = Math.exp(-depth/25.0);
		switch (this) {
		case ROCK_FLOOR: return 1 - ((1 - 0.53) * factor);
		case DUNGEON_FLOOR: return 1 - ((1 - 0.58) * factor);
		default: return 1.0;
		}
	}
	public double admitsThinGrass(int depth) {
		double factor = Math.exp(-depth/25.0);
		switch (this) {
		case ROCK_FLOOR: return 1 - ((1 - 0.48) * factor);
		case DUNGEON_FLOOR: return 1 - ((1 - 0.53) * factor);
		default: return 1.0;
		}
	}
	public double admitsFungus(int depth) {
		if (depth < 12) return 1.0;
		depth -= 12;
		double factor = Math.exp(-depth/50.0);
		switch (this) {
		case ROCK_FLOOR: return 1 - ((1 - 0.55) * factor);
		case DUNGEON_FLOOR: return 1 - ((1 - 0.60) * factor);
		default: return 1.0;
		}
	}
	public boolean isGrass() {
		return this == THICK_GRASS_R || this == THICK_GRASS_S ||
				this == THIN_GRASS_R || this == THIN_GRASS_S;
	}
	public Terrain getThickGrass() {
		if (getBackColor() == C.rockFloor)
			return THICK_GRASS_R;
		else if (getBackColor() == C.stoneFloor)
			return THICK_GRASS_S;
		else throw new IllegalStateException();
	}
	public Terrain getThinGrass() {
		if (getBackColor() == C.rockFloor)
			return THIN_GRASS_R;
		else if (getBackColor() == C.stoneFloor)
			return THIN_GRASS_S;
		else throw new IllegalStateException();
	}
	public Terrain getMushrooms() {
		if (getBackColor() == C.rockFloor)
			return MUSHROOMS_R;
		else if (getBackColor() == C.stoneFloor)
			return MUSHROOMS_S;
		else throw new IllegalStateException();
	}
	
	public char getCharacter() {
		return character;
	}
	public Color getForeColor() {
		return fgColor;
	}
	public Color getBackColor() {
		return bgColor;
	}
	public String getDescription() {
		return description;
	}
	
}
