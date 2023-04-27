package org.canvacord.util;

import java.awt.*;

public class CanvaCordColors {

	public static Color decode(String colorStr) {

		if (colorStr == null || colorStr.length() != 8)
			throw new IllegalStateException("Color string must be 8 characters long");

		int r, g, b, a;
		a = Integer.parseInt(colorStr.substring(0, 2), 16);
		r = Integer.parseInt(colorStr.substring(2, 4), 16);
		g = Integer.parseInt(colorStr.substring(4, 6), 16);
		b = Integer.parseInt(colorStr.substring(6, 8), 16);

		return new Color(r, g, b, a);

	}

}
