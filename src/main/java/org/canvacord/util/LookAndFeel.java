package org.canvacord.util;

import javax.swing.*;

public class LookAndFeel {

	/**
	 * Attempt to set the Look and Feel of Swing components to the user's native system appearance.
	 */
	public static void init() {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {}
	}

}
