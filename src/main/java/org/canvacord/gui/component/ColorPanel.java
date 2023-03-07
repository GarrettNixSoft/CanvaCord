package org.canvacord.gui.component;

import org.checkerframework.checker.units.qual.C;

import javax.swing.*;
import java.awt.*;

public class ColorPanel extends JPanel {

	private Color color;
	private boolean doBorder;

	public ColorPanel(Color color) {
		this.color = color;
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
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (doBorder) {
			Color borderColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
			g.setColor(borderColor);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

}
