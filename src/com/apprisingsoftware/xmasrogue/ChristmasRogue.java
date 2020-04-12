package com.apprisingsoftware.xmasrogue;

import com.apprisingsoftware.xmasrogue.io.RoguePanel;
import javax.swing.JFrame;

public final class ChristmasRogue {
	
	private ChristmasRogue() {}
	
	public static void main(String[] args) {
		RoguePanel panel = new RoguePanel(120, 36);
		panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.setVisible(true);
	}
	
	private static boolean DEBUG = false;
	public static void setDEBUG(boolean val) {
		DEBUG = val;
	}
	public static boolean DEBUG() {
		return DEBUG;
	}
	
}
