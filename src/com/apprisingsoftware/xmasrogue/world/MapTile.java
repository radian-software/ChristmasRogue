package com.apprisingsoftware.xmasrogue.world;

import com.apprisingsoftware.util.MathUtil;
import com.apprisingsoftware.xmasrogue.entity.Entity;
import com.apprisingsoftware.xmasrogue.entity.Item;
import com.apprisingsoftware.xmasrogue.io.AsciiTile;
import java.awt.Color;

public final class MapTile {
	
	public enum Shading {
		UNDISCOVERED,
		DISCOVERED,
		VISIBLE,
		;
	}
	
	private Terrain terrain;
	private Entity entity;
	private Color backColor;
	private Item item;
	private Shading shading;
	
	public MapTile(Terrain terrain) {
		if (terrain == null) throw new IllegalArgumentException();
		setTerrain(terrain);
		shading = Shading.UNDISCOVERED;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	public void setTerrain(Terrain newTerrain) {
		terrain = newTerrain;
		backColor = randomizeColor(terrain.getBackColor());
	}
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity newEntity) {
		entity = newEntity;
	}
	public void removeEntity() {
		setEntity(null);
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item newItem) {
		item = newItem;
	}
	public void removeItem() {
		setItem(null);
	}
	public Shading getShading() {
		return shading;
	}
	public void setShading(Shading newShading) {
		shading = newShading;
	}
	
	public AsciiTile getAsciiTile() {
		if (entity != null) {
			return new AsciiTile(entity.getCharacter(), darkenColor(entity.getColor(), shading), darkenColor(backColor, shading));
		}
		else if (item != null) {
			return new AsciiTile(item.getCharacter(), darkenColor(item.getColor(), shading), darkenColor(backColor, shading));
		}
		else {
			return new AsciiTile(terrain.getCharacter(), darkenColor(terrain.getForeColor(), shading), darkenColor(backColor, shading));
		}
	}
	
	private Color randomizeColor(Color original) {
		return randomizeColor(original, 0.1, 0.1, 0.1);
	}
	private Color randomizeColor(Color original, double hueAmount, double satAmount, double briAmount) {
		float[] hsb = Color.RGBtoHSB(original.getRed(), original.getGreen(), original.getBlue(), null);
		hsb[0] *= MathUtil.random(1 - hueAmount, 1 + hueAmount);
		hsb[1] *= MathUtil.random(1 - satAmount, 1 + satAmount);
		hsb[2] *= MathUtil.random(1 - briAmount, 1 + briAmount);
		hsb = new float[] {
				MathUtil.clamp(0, hsb[0], 1),
				MathUtil.clamp(0, hsb[1], 1),
				MathUtil.clamp(0, hsb[2], 1)
		};
		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
	}
	
	private Color darkenColor(Color original, Shading shading) {
		switch (shading) {
		case UNDISCOVERED: return darkenColor(original, 0.0);
		case DISCOVERED: return darkenColor(original, 0.5);
		case VISIBLE: return darkenColor(original, 1.0);
		default: throw new AssertionError();
		}
	}
	private Color darkenColor(Color original, double percentage) {
		float[] hsb = Color.RGBtoHSB(original.getRed(), original.getGreen(), original.getBlue(), null);
		hsb[2] *= percentage;
		hsb = new float[] {
				MathUtil.clamp(0, hsb[0], 1),
				MathUtil.clamp(0, hsb[1], 1),
				MathUtil.clamp(0, hsb[2], 1)
		};
		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
	}
	
}
