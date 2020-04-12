package com.apprisingsoftware.xmasrogue.io;

import java.awt.event.KeyEvent;

public interface InputAcceptor {
	
	/**
	 * Respond to keyboard input in some way, typically by changing
	 * the state of the screen. If information -- for instance,
	 * directions to change the screens visible on a CascadedAsciiScreen --
	 * must be returned, use a specialized method instead of the
	 * InputAcceptor interface method.
	 */
	void respondToInput(KeyEvent e);
	
}
