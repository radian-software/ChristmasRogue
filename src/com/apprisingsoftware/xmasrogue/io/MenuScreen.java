package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.util.ArrayUtil;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.Color;
import java.util.Collection;
import java.util.Map;

public abstract class MenuScreen implements AsciiScreen {
	
	protected final int width, height;
	private final Map<Coord, Message> labels;
	private boolean justCreated;
	
	public MenuScreen(int width, int height, Map<Coord, Message> labels) {
		this.width = width;
		this.height = height;
		this.labels = labels;
		justCreated = true;
	}
	
	@Override public AsciiTile getTile(int x, int y) {
		for (Map.Entry<Coord, Message> entry : labels.entrySet()) {
			Coord loc = entry.getKey();
			Message label = entry.getValue();
			if (y == loc.y && x >= loc.x && x < loc.x + label.getText().length()) {
				return new AsciiTile(label.getText().charAt(x - loc.x), label.getActiveColor(), getBackgroundColor(x, y));
			}
		}
		return new AsciiTile(' ', Color.BLACK, getBackgroundColor(x, y));
	}
	protected abstract Color getBackgroundColor(int x, int y);
	@Override public boolean isTransparent(int x, int y) {
		return !inBounds(x, y);
	}
	
	@Override public Collection<Coord> getUpdatedTiles() {
		if (justCreated) {
			justCreated = false;
			return ArrayUtil.cartesianProduct(width, height, (x, y) -> new Coord(x, y));
		}
		return getUpdatedBackgroundTiles();
	}
	public abstract Collection<Coord> getUpdatedBackgroundTiles();
	
	@Override public int getWidth() {
		return width;
	}
	@Override public int getHeight() {
		return height;
	}
	
}
