package com.apprisingsoftware.xmasrogue.io;

import com.apprisingsoftware.xmasrogue.ChristmasRogue;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class VictoryScreen extends MenuScreen implements NestedInputAcceptingAsciiScreen {
	
	public VictoryScreen(int width, int height) {
		super(width, height, new HashMap<Coord, Message>() {{
			char[] title = {(char)17,' ',' ','M','E','R','R','Y',' ','C','H','R','I','S','T','M','A','S',' ',' ',(char)16};
			for (int i=0; i<title.length; i++) {
				put(new Coord((width - title.length) / 2 + i, 3), new Message(String.valueOf(title[i]), i%2==0 ? Color.RED : Color.GREEN));
			}
			String[] story = {
					"A WINNER IS YOU",
					"",
					"Congratulations -- you've saved Christmas!",
					"",
					"[n] New Game",
					"[w] Wizard Mode",
			};
			int maxLen = Arrays.stream(story).mapToInt(String::length).max().getAsInt();
			for (int i=0; i<story.length; i++) {
				put(new Coord((width - maxLen) / 2, 5 + i), new Message(story[i], Color.WHITE));
			}
		}});
	}
	
	@Override protected Color getBackgroundColor(int x, int y) {
		return Color.BLACK;
	}
	
	@Override public Collection<Coord> getUpdatedBackgroundTiles() {
		return Collections.emptyList();
	}
	
	@Override public NestedInputAcceptingAsciiScreen respondToInput(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_N) {
			ChristmasRogue.setDEBUG(false);
			return new GameAsciiScreen(width, height, (int) System.currentTimeMillis());
		}
		else if (e.getKeyCode() == KeyEvent.VK_W) {
			ChristmasRogue.setDEBUG(true);
			return new GameAsciiScreen(width, height, (int) System.currentTimeMillis());
		}
		return null;
	}
	
}
