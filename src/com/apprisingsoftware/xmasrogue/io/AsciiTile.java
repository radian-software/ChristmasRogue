package com.apprisingsoftware.xmasrogue.io;

import java.awt.Color;

public final class AsciiTile {
	
	private char glyph;
	private Color foreground, background;
	
	public AsciiTile(char glyph, Color foreground, Color background) {
		this.glyph = glyph;
		this.foreground = foreground;
		this.background = background;
	}
	
	public char getCharacter() {
		return glyph;
	}
	public Color getForeColor() {
		return foreground;
	}
	public Color getBackColor() {
		return background;
	}
	
}
