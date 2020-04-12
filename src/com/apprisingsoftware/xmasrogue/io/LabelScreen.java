package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.util.ArrayUtil;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.Color;
import java.util.Collection;
import java.util.Collections;

public final class LabelScreen implements AsciiScreen {
	
	public enum Alignment {
		LEFT,
		RIGHT,
		CENTER
	}
	
	private final int width;
	private final Color bgColor;
	private final Alignment align;
	
	private Message message;
	private boolean hasBeenUpdated;
	
	public LabelScreen(int width, Color bgColor, Alignment alignment) {
		this.width = width;
		this.bgColor = bgColor;
		this.align = alignment;
		hasBeenUpdated = true;
	}
	
	public void postMessage(Message message) {
		if (message.getText().length() > width) throw new IllegalArgumentException(String.format("\"%s\" is too long of a message (more than %d characters)", message.getText(), width));
		this.message = message;
		hasBeenUpdated = true;
	}
	
	@Override public AsciiTile getTile(int x, int y) {
		int mx;
		switch (align) {
		case LEFT:
			mx = x;
			break;
		case RIGHT:
			mx = x - (width - message.getText().length());
			break;
		case CENTER:
			mx = Math.floorDiv(2 * x + message.getText().length() - width, 2);
			break;
		default: throw new AssertionError();
		}
		if (mx >= 0 && mx < message.getText().length()) {
			return new AsciiTile(message.getText().charAt(mx), message.getActiveColor(), bgColor);
		}
		else {
			return new AsciiTile(' ', Color.BLACK, bgColor);
		}
	}
	
	@Override public boolean isTransparent(int x, int y) {
		return !inBounds(x, y);
	}
	
	@Override public Collection<Coord> getUpdatedTiles() {
		if (hasBeenUpdated) {
			hasBeenUpdated = false;
			return ArrayUtil.cartesianProduct(width, 1, (x, y) -> new Coord(x, y));
		}
		else return Collections.emptyList();
	}
	
	@Override public int getWidth() {
		return width;
	}
	@Override public int getHeight() {
		return 1;
	}
	
}
