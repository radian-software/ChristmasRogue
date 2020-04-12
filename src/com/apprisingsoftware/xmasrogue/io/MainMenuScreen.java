package com.apprisingsoftware.xmasrogue.io;

import asciiPanel.AsciiPanel;
import com.apprisingsoftware.xmasrogue.ChristmasRogue;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class MainMenuScreen extends MenuScreen implements NestedInputAcceptingAsciiScreen {
	
	public MainMenuScreen(int width, int height) {
		super(width, height, new HashMap<Coord, Message>() {{
			char[] title = {(char)17,' ',' ','M','E','R','R','Y',' ','C','H','R','I','S','T','M','A','S',' ',' ',(char)16};
			for (int i=0; i<title.length; i++) {
				put(new Coord((width - title.length) / 2 + i, 3), new Message(String.valueOf(title[i]), i%2==0 ? Color.RED : Color.GREEN));
			}
			String[] story = {
					"It's Christmas Eve and almost time for Santa Claus to give out presents. All is ready for tomorrow...",
					"But wait! Santa's sleigh is gone! Nailed to the door (how inconsiderate -- it was just recently polished!)",
					"reads a note:",
					"",
					"    Dear Mr. Claus,",
					"        We, the Foundation for the Promotion of Monstrous Rights and Privileges, believe that your",
					"    'gift-giving' operation is blatantly discriminatory against our constituency. Last year, not a single",
					"    corrupt elf or vampire bat received a present despite the fact that your extensive operation could",
					"    clearly support their inclusion. We feel that your official statement on the issue, involving such",
					"    subjective and easily biased terms as 'naughty' and 'nice', is completely unsatisfactory.",
					"        Since your prejudicial pandering to the human bloc has made it impossible for the FPMRP to seek",
					"    legislation to address our concerns, we have been forced to take more drastic action. You may have",
					"    noticed that your sleigh is missing. It currently resides at the twenty-sixth level of the Dark",
					"    Catacombs. We and our constituency earnestly hope that our actions will render Christmas impossible,",
					"    this year, in support of continued progress toward inter-species equality in the future.",
					"",
					"    Sincerely,",
				    "        Mr. Shamilith, Ancient Karmic Wyrm and President of the FPMRP",
				    "",
				    "Can you help Santa save Christmas in time? (I sure hope so -- I want my presents.)",
				    "",
				    "[n] New Game",
			};
			int maxLen = Arrays.stream(story).mapToInt(String::length).max().getAsInt();
			for (int i=0; i<story.length; i++) {
				put(new Coord((width - maxLen) / 2, 5 + i), new Message(story[i], Color.WHITE));
			}
		}});
	}
	
	@Override public NestedInputAcceptingAsciiScreen respondToInput(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_N) {
			ChristmasRogue.setDEBUG(false);
			return new GameAsciiScreen(width, height, (int) System.currentTimeMillis());
		}
		if (e.getKeyCode() == KeyEvent.VK_W) {
			ChristmasRogue.setDEBUG(true);
			return new GameAsciiScreen(width, height, (int) System.currentTimeMillis());
		}
		return null;
	}
	
	@Override protected Color getBackgroundColor(int x, int y) {
		return AsciiPanel.black;
	}
	
	@Override public Collection<Coord> getUpdatedBackgroundTiles() {
		return Collections.emptyList();
	}
	
}
