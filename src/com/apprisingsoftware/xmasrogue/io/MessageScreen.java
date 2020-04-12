package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MessageScreen implements AsciiScreen {
	
	private static final String moreMessage = "-- MORE --";
	private static final Color[] moreMessageColors = {Color.BLACK, Color.WHITE};
	
	protected final int width, numMessages;
	private final LinkedList<Message> messages;
	private final LinkedList<Boolean> areActive;
	private final Color bgColor;
	private boolean hasBeenModified = true;
	private boolean showingMorePrompt = false;
	
	public MessageScreen(int width, int numMessages, Color bgColor) {
		this.width = width;
		this.numMessages = numMessages;
		this.messages = new LinkedList<>(Collections.nCopies(numMessages, null));
		this.areActive = new LinkedList<>(Collections.nCopies(numMessages, false));
		this.bgColor = bgColor;
	}
	
	public Message pushMessage(Message message) {
		if (message.getText().length() > width - moreMessage.length()) throw new IllegalArgumentException("\"" + message.getText() + "\" is too long of a message");
		hasBeenModified = true;
		messages.addLast(message);
		areActive.addLast(true);
		if (messages.size() > numMessages) {
			areActive.removeFirst();
			return messages.removeFirst();
		}
		else return null;
	}
	public void dilapidateOldMessages() {
		for (int i=0; i<areActive.size(); i++) {
			if (areActive.get(i)) {
				hasBeenModified = true;
			}
			areActive.set(i, false);
		}
	}
	public void showMorePrompt() {
		showingMorePrompt = true;
		hasBeenModified = true;
	}
	public void hideMorePrompt() {
		showingMorePrompt = false;
		hasBeenModified = true;
	}
	
	public int getNumMessages() {
		return numMessages;
	}
	
	@Override public AsciiTile getTile(int x, int y) {
		if (showingMorePrompt && y == numMessages - 1 && width-1 - x >= 0 && width-1 - x < moreMessage.length()) {
			return new AsciiTile(moreMessage.charAt(moreMessage.length()-1 - (width-1 - x)), moreMessageColors[0], moreMessageColors[1]);
		}
		Message message = messages.get(y);
		boolean active = areActive.get(y);
		if (message == null || x >= message.getText().length()) {
			return new AsciiTile(' ', Color.BLACK, bgColor);
		}
		else {
			return new AsciiTile(message.getText().charAt(x), active ? message.getActiveColor() : message.getInactiveColor(), bgColor);
		}
	}
	@Override public boolean isTransparent(int x, int y) {
		return !inBounds(x, y);
	}
	
	@Override public Collection<Coord> getUpdatedTiles() {
		if (hasBeenModified) {
			hasBeenModified = false;
			return IntStream.range(0, width).boxed().flatMap(x -> IntStream.range(0, numMessages).<Coord>mapToObj(y -> new Coord(x, y))).collect(Collectors.toList());
		}
		else return Collections.emptyList();
	}
	
	@Override public int getWidth() {
		return width;
	}
	
	@Override public int getHeight() {
		return numMessages;
	}
	
}
