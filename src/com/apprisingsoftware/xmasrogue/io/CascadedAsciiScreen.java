package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.util.Pair;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public abstract class CascadedAsciiScreen implements AsciiScreen {
	
	protected final int width, height;
	
	private final List<AsciiScreen> subscreens;
	private final List<Coord> subscreenOffsets;
	private final List<Set<Coord>> subscreenUpdatedTiles;
	private final boolean[][] unmaskedTiles;
	
	public CascadedAsciiScreen(int width, int height) {
		this.width = width;
		this.height = height;
		this.subscreens = new ArrayList<>();
		this.subscreenOffsets = new ArrayList<>();
		this.subscreenUpdatedTiles = new ArrayList<>();
		this.unmaskedTiles = new boolean[width][height];
	}
	
	protected void addOrSetScreens(List<Pair<AsciiScreen, Coord>> screens, int startingLayer) {
		for (int l=0; l<+screens.size(); l++) {
			if (l + startingLayer < subscreens.size()) {
				setScreen(l + startingLayer, screens.get(l).getFirst(), screens.get(l).getSecond().x, screens.get(l).getSecond().y);
			}
			else if (l + startingLayer == subscreens.size()) {
				addScreen(screens.get(l).getFirst(), screens.get(l).getSecond().x, screens.get(l).getSecond().y);
			}
			else {
				throw new IllegalStateException();
			}
		}
	}
	protected void addScreen(AsciiScreen screen, int x, int y) {
		ensureScreenFits(screen, x, y);
		subscreens.add(screen);
		subscreenOffsets.add(new Coord(x, y));
		subscreenUpdatedTiles.add(new HashSet<>());
	}
	protected void insertScreen(int layer, AsciiScreen screen, int x, int y) {
		ensureScreenFits(screen, x, y);
		subscreens.add(layer, screen);
		subscreenOffsets.add(layer, new Coord(x, y));
		subscreenUpdatedTiles.add(layer, new HashSet<>());
	}
	protected void setScreen(int layer, AsciiScreen screen, int x, int y) {
		ensureScreenFits(screen, x, y);
		subscreens.set(layer, screen);
		subscreenOffsets.set(layer, new Coord(x, y));
		subscreenUpdatedTiles.set(layer, new HashSet<>());
	}
	protected void removeScreen(int layer) {
		AsciiScreen screen = subscreens.get(layer);
		Coord offset = subscreenOffsets.get(layer);
		for (int lx=offset.x; lx<offset.x+screen.getWidth(); lx++) {
			for (int ly=offset.y; ly<offset.y+screen.getHeight(); ly++) {
				unmaskedTiles[lx][ly] = true;
			}
		}
		subscreens.remove(layer);
		subscreenOffsets.remove(layer);
		subscreenUpdatedTiles.remove(layer);
	}
	protected boolean removeScreen(AsciiScreen screen) {
		try {
			removeScreen(subscreens.indexOf(screen));
			return true;
		}
		catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	protected void removeTopScreen() {
		removeScreen(subscreens.size()-1);
	}
	protected void removeBottomScreen() {
		removeScreen(0);
	}
	private void ensureScreenFits(AsciiScreen screen, int x, int y) {
		if (x < 0 || y < 0 || x + screen.getWidth() > width || y + screen.getHeight() > height) {
			throw new IndexOutOfBoundsException();
		}
	}
	
	protected int depth() {
		return subscreens.size();
	}
	
	/**
	 * Determines if the specified coordinate (x, y) is not covered
	 * by any layer at a height higher than the one provided. In other
	 * words, if the specified coordinate of the specified layer is
	 * visible from the top.
	 */
	public boolean isVisible(int x, int y, int layer) {
		for (int l=subscreens.size()-1; l>layer; l--) {
			AsciiScreen screen = subscreens.get(l);
			Coord offset = subscreenOffsets.get(l);
			if (!screen.isTransparent(x - offset.x, y - offset.y)) {
				return false;
			}
		}
		return true;
	}
	
	// AsciiScreen
	@Override public AsciiTile getTile(int x, int y) {
		for (int l=depth()-1; l>-1; l--) {
			AsciiScreen screen = subscreens.get(l);
			Coord offset = subscreenOffsets.get(l);
			if (!screen.isTransparent(x - offset.x, y - offset.y)) {
				return screen.getTile(x - offset.x, y - offset.y);
			}
		}
		throw new IndexOutOfBoundsException();
	}
	@Override public boolean isTransparent(int x, int y) {
		return !inBounds(x, y) || IntStream.range(0, depth()).allMatch(
				i -> subscreens.get(i).isTransparent(x - subscreenOffsets.get(i).x, y - subscreenOffsets.get(i).y)
		);
	}
	
	@Override public Collection<Coord> getUpdatedTiles() {
		Collection<Coord> updatedTiles = new ArrayList<>();
		for (int l=0; l<depth(); l++) {
			subscreenUpdatedTiles.get(l).addAll(subscreens.get(l).getUpdatedTiles());
			Iterator<Coord> it = subscreenUpdatedTiles.get(l).iterator();
			while (it.hasNext()) {
				Coord subscreenUpdatedTile = it.next().plus(subscreenOffsets.get(l));
				if (isVisible(subscreenUpdatedTile.x, subscreenUpdatedTile.y, l)) {
					updatedTiles.add(subscreenUpdatedTile);
					it.remove();
				}
			}
		}
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				if (unmaskedTiles[x][y]) {
					unmaskedTiles[x][y] = false;
					if (!updatedTiles.contains(new Coord(x, y))) {
						updatedTiles.add(new Coord(x, y));
					}
				}
			}
		}
		return updatedTiles;
	}
	
	@Override public int getWidth() {
		return width;
	}
	@Override public int getHeight() {
		return height;
	}
	
}
