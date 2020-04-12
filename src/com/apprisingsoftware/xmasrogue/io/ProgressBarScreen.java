package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.util.NormalDoubleSupplier;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.Color;
import java.util.Collection;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ProgressBarScreen implements AsciiScreen {
	
	private final int length;
	private final Color fullColor, emptyColor;
	private final NormalDoubleSupplier value;
	private final String text;
	private final Color textColor;
	private final int textOffset;
	
	private boolean justCreated;
	private int lastCell;
	
	public ProgressBarScreen(int length, Color fullColor, Color emptyColor, DoubleSupplier value, String text, Color textColor, int textOffset) {
		if (length <= 0 || fullColor == null || emptyColor == null || value == null) throw new IllegalArgumentException();
		this.length = length;
		this.fullColor = fullColor;
		this.emptyColor = emptyColor;
		this.value = new NormalDoubleSupplier(value);
		this.text = text;
		this.textColor = textColor;
		this.textOffset = textOffset;
		justCreated = true;
		lastCell = length - 1;
	}
	public ProgressBarScreen(int length, Color fullColor, Color emptyColor, DoubleSupplier value, String text, Color textColor) {
		this(length, fullColor, emptyColor, value, text, textColor, (length - text.length()) / 2);
	}
	
	private static Color getIntermediateColor(Color zeroColor, Color oneColor, double factor) {
		if (zeroColor == null || oneColor == null || factor < 0 || factor > 1) throw new IllegalArgumentException();
		float[] zeroHSB = Color.RGBtoHSB(zeroColor.getRed(), zeroColor.getGreen(), zeroColor.getBlue(), null);
		float[] oneHSB = Color.RGBtoHSB(oneColor.getRed(), oneColor.getGreen(), oneColor.getBlue(), null);
		float[] intermediateHSB = {
				(float) (zeroHSB[0] + (oneHSB[0] - zeroHSB[0]) * factor),
				(float) (zeroHSB[1] + (oneHSB[1] - zeroHSB[1]) * factor),
				(float) (zeroHSB[2] + (oneHSB[2] - zeroHSB[2]) * factor)
		};
		int intermediateColor = Color.HSBtoRGB(intermediateHSB[0], intermediateHSB[1], intermediateHSB[2]);
		return new Color(intermediateColor);
	}
	
	@Override public AsciiTile getTile(int x, int y) {
		double fraction = value.get();
		int currentCell = fraction != 1 ? (int) (length * fraction) : length - 1;
		Color bgColor;
		if (x < currentCell) bgColor = fullColor;
		else if (x > currentCell) bgColor = emptyColor;
		else bgColor = getIntermediateColor(emptyColor, fullColor, fraction != 1 ? length * fraction % 1 : 1); // x % 1 -> fPart(x)
		return new AsciiTile(charAt(x), textColor, bgColor);
	}
	private char charAt(int x) {
		try {
			return text.charAt(x - textOffset);
		}
		catch (StringIndexOutOfBoundsException e) {
			return ' ';
		}
	}
	@Override public boolean isTransparent(int x, int y) {
		return !inBounds(x, y);
	}
	
	@Override public Collection<Coord> getUpdatedTiles() {
		if (justCreated) {
			justCreated = false;
			return IntStream.range(0, length).mapToObj(x -> new Coord(x, 0)).collect(Collectors.toList());
		}
		else {
			int oneEnd = lastCell;
			this.lastCell = value.get() != 1 ? (int) (length * value.get()) : length - 1;
			int otherEnd = lastCell;
			// Ensure that oneEnd < otherEnd so that rangeClosed will work properly
			if (oneEnd > otherEnd) {
				oneEnd = (otherEnd ^= oneEnd ^= otherEnd) ^ oneEnd;
				// See https://en.wikipedia.org/wiki/XOR_swap_algorithm
			}
			return IntStream.rangeClosed(oneEnd, otherEnd).mapToObj(x -> new Coord(x, 0)).collect(Collectors.toList());
		}
	}
	
	@Override public int getWidth() {
		return length;
	}
	
	@Override public int getHeight() {
		return 1;
	}
	
}
