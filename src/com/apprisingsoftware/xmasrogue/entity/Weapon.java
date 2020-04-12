package com.apprisingsoftware.xmasrogue.entity;

import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Weapon implements Item {
	
	private final String name;
	private final List<Attack> attacks;
	
	public Weapon(String name, List<Attack> attacks) {
		this.name = name;
		this.attacks = attacks;
	}
	
	@Override public char getCharacter() {
		return (char)24;
	}
	@Override public Color getColor() {
		return Color.WHITE;
	}
	@Override public String getName() {
		return name;
	}
	public Attack getAttack(Random random) {
		return Attack.selectAttack(attacks, random);
	}
	
}
