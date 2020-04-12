package com.apprisingsoftware.xmasrogue.io;

import java.awt.Color;

public final class Message {
	
	public static final Color FAILURE_COLOR = Color.WHITE;
	public static final Color SPEECH_COLOR = Color.WHITE;
	public static final Color PLAYER_DAMAGE_COLOR = Color.WHITE;
	public static final Color MONSTER_DAMAGE_COLOR = Color.WHITE;
	public static final Color MENU_COLOR = Color.WHITE;
	public static final Color STATUS_COLOR = Color.WHITE;
	
	private final String text;
	private final Color textColor;
	
	public Message(String text, Color color) {
		this.text = text;
		this.textColor = color;
	}
	
	public String getText() {
		return text;
	}
	public Color getActiveColor() {
		return textColor;
	}
	public Color getInactiveColor() {
		float[] cHSB = Color.RGBtoHSB(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), null);
		cHSB[2] *= 0.7f;
		int inactiveColor = Color.HSBtoRGB(cHSB[0], cHSB[1], cHSB[2]);
		return new Color(inactiveColor);
	}
	
	// Object
	@Override public String toString() {
		return String.format("\"%s\"", text);
	}
	
}
