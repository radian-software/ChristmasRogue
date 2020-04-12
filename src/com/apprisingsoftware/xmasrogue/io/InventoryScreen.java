package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.util.ArrayUtil;
import com.apprisingsoftware.xmasrogue.entity.Inventory;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InventoryScreen implements AsciiScreen {
	
	private final List<String> itemNames;
	private static final int margin = 1;
	private int maxLength;
	private boolean justCreated;
	
	public InventoryScreen(Inventory inventory, String title) {
		itemNames = inventory.getItemNames();
		maxLength = title.length() + 4;
		for (int i=0; i<itemNames.size(); i++) {
			maxLength = Math.max(maxLength, itemNames.get(i).length() + 4);
		}
		for (int i=0; i<itemNames.size(); i++) {
			itemNames.set(i, String.format("%c %s%s %c", (char)186, itemNames.get(i), ArrayUtil.nString(' ', Math.max(0, maxLength - (itemNames.get(i).length() + 4))), (char)186));
			maxLength = Math.max(maxLength, itemNames.get(i).length());
		}
		itemNames.add(0, String.format("%c%s%c", (char)201, ArrayUtil.nString((char)205, maxLength - 2), (char)187));
		itemNames.add(1, String.format("%c %s %s%c", (char)186, title, ArrayUtil.nString(' ', maxLength - (title.length() + 4)), (char)186));
		itemNames.add(2, String.format("%c%s%c", (char)204, ArrayUtil.nString((char)205, maxLength - 2), (char)185));
		itemNames.add(String.format("%c%s%c", (char)200, ArrayUtil.nString((char)205, maxLength - 2), (char)188));
		justCreated = true;
	}
	
	public boolean isEmpty() {
		return itemNames.size() == 4;
	}
	
	@Override public AsciiTile getTile(int x, int y) {
		try {
			return new AsciiTile(itemNames.get(y).charAt(x - margin), Message.MENU_COLOR, Color.BLACK);
		}
		catch (IndexOutOfBoundsException e) {
			return new AsciiTile(' ', Color.BLACK, Color.BLACK);
		}
	}
	
	@Override public boolean isTransparent(int x, int y) {
		return !inBounds(x, y);
	}
	
	@Override public Collection<Coord> getUpdatedTiles() {
		return justCreated | (justCreated = false) ?
				ArrayUtil.cartesianProduct(maxLength + margin * 2, itemNames.size(), (x, y) -> new Coord(x, y)) :
					Collections.emptyList();
	}
	
	@Override public int getWidth() {
		return maxLength + margin * 2;
	}
	@Override public int getHeight() {
		return 2 + itemNames.size();
	}
	
}
