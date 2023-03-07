package org.canvacord.gui.component;

import javax.swing.*;
import java.awt.*;

public class ColorIcon implements Icon {

	private Color color;
	private final int width;
	private final int height;
	private boolean doBorder;

	public ColorIcon(Color color, int width, int height) {
		this.color = color;
		this.width = width;
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setDoBorder(boolean doBorder) {
		this.doBorder = doBorder;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(x, y, width, height);

		if (doBorder) {
			Color borderColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
			g.setColor(borderColor);
			g.drawRect(x, y, width - 1, height - 1);
		}
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}
}
