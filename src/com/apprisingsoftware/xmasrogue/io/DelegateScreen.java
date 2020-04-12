package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class DelegateScreen implements InputAcceptingAsciiScreen {
	
	protected final int width, height;
	private NestedInputAcceptingAsciiScreen subscreen;
	private boolean justSwapped;
	
	public DelegateScreen(int width, int height, NestedInputAcceptingAsciiScreen initialScreen) {
		this.width = width;
		this.height = height;
		setScreen(initialScreen);
		this.justSwapped = true;
	}
	
	private void setScreen(NestedInputAcceptingAsciiScreen screen) {
		if (width != screen.getWidth() || height != screen.getHeight()) {
			throw new IllegalArgumentException();
		}
		subscreen = screen;
	}
	
	@Override public AsciiTile getTile(int x, int y) {
		return subscreen.getTile(x, y);
	}
	@Override public boolean isTransparent(int x, int y) {
		return subscreen.isTransparent(x, y);
	}
	
	@Override public Collection<Coord> getUpdatedTiles() {
		if (justSwapped) {
			justSwapped = false;
			return IntStream.range(0, width).boxed().flatMap(x -> IntStream.range(0, height).<Coord>mapToObj(y -> new Coord(x, y))).collect(Collectors.toList());
		}
		return subscreen.getUpdatedTiles();
	}
	
	@Override public int getWidth() {
		return width;
	}
	@Override public int getHeight() {
		return height;
	}

	@Override public void respondToInput(KeyEvent e) {
		NestedInputAcceptingAsciiScreen newScreen = subscreen.respondToInput(e);
		if (newScreen != null && newScreen != subscreen) {
			subscreen = newScreen;
			justSwapped = true;
		}
	}
	
}
