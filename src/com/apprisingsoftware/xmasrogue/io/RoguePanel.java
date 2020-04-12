package com.apprisingsoftware.xmasrogue.io;

import asciiPanel.AsciiPanel;
import com.apprisingsoftware.xmasrogue.util.Coord;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public final class RoguePanel extends JFrame {
	
	private static final Color transparencyColor = Color.BLACK;
	
	private final AsciiPanel terminal;
	private final InputAcceptingAsciiScreen screen;
	
	public RoguePanel(int width, int height) {
		super("Christmas Rogue");
		this.terminal = new AsciiPanel(width, height);
		add(terminal);
		pack();
		this.screen = new DelegateScreen(width, height, new MainMenuScreen(width, height));
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(e -> {
			try {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					screen.respondToInput(e);
					repaint();
				}
			}
			catch (Throwable e1) {
				logError(e1);
			}
			return false;
		});
		repaint();
	}
	
	// JFrame
	@Override public void repaint() {
		try {
			for (Coord cell : screen.getUpdatedTiles()) {
				if (screen.isTransparent(cell.x, cell.y)) {
					terminal.write(' ', cell.x, cell.y, AsciiPanel.black, transparencyColor);
				}
				else {
					AsciiTile tile = screen.getTile(cell.x, cell.y);
					terminal.write(tile.getCharacter(), cell.x, cell.y, tile.getForeColor(), tile.getBackColor());
				}
			}
			super.repaint();
		}
		catch (Throwable e) {
			logError(e);
		}
	}
	
	private void logError(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String error = sw.toString();
		JOptionPane.showMessageDialog(null, error, "Oh noes!!!1!", JOptionPane.ERROR_MESSAGE);
	}
	
}
