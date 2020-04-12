package com.apprisingsoftware.xmasrogue.io;

import java.awt.event.KeyEvent;

public interface NestedInputAcceptingAsciiScreen extends AsciiScreen {
	
	NestedInputAcceptingAsciiScreen respondToInput(KeyEvent e);
	
}
