package org.canvacord.gui;

import java.awt.*;

public class CanvaCordFonts {


	public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
	public static final Font LABEL_FONT_TINY = new Font("Segoe UI", Font.PLAIN, 10);
	public static final Font LABEL_FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
	public static final Font LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Font LABEL_FONT_MEDIUM = new Font("Segoe UI", Font.PLAIN, 14);
	public static final Font LABEL_FONT_LARGE = new Font("Segoe UI", Font.PLAIN, 16);

	/**
	 * Generate a copy of the given font, in bold style.
	 * @param font the base font
	 * @return a bold version of the base font
	 */
	public static Font bold(Font font) {
		return font.deriveFont(Font.BOLD);
	}

	/**
	 * Generate a copy of the given font, in italic style.
	 * @param font the base font
	 * @return an italicized version of the base font
	 */
	public static Font italic(Font font) {
		return font.deriveFont(Font.ITALIC);
	}

}
