package com.apprisingsoftware.xmasrogue.entity;

import java.awt.Color;

public final class Sleigh implements Item {
	
	public Sleigh() {}
	
	@Override public char getCharacter() {
		return (char)28;
	}
	
	@Override public Color getColor() {
		return Color.YELLOW;
	}
	
	@Override public String getName() {
		return "magical sleigh";
	}
	
}
