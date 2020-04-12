package com.apprisingsoftware.xmasrogue.entity;

import java.awt.Color;

public class Armor implements Item {
	
	private final String name;
	private final double damageBlocked;
	
	public Armor(String name, double damageBlocked) {
		this.name = name;
		this.damageBlocked = damageBlocked;
	}
	
	@Override public char getCharacter() {
		return ']';
	}
	@Override public Color getColor() {
		return Color.WHITE;
	}
	@Override public String getName() {
		return name;
	}
	public double reduceDamage(double damage) {
		return damage * (1 - damageBlocked);
	}
	
}
