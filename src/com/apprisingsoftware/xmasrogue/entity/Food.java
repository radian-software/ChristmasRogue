package com.apprisingsoftware.xmasrogue.entity;

import java.awt.Color;

public final class Food implements Item {
	
	private final String name;
	private final int hungerRestored;
	
	public Food(String name, int hungerRestored) {
		this.name = name;
		this.hungerRestored = hungerRestored;
	}
	
	public int getNutrition() {
		return hungerRestored;
	}
	
	@Override public char getCharacter() {
		return (char)21;
	}
	
	@Override public Color getColor() {
		return Color.WHITE;
	}
	
	@Override public String getName() {
		return name;
	}
	
}
