package org.canvacord.gui.component;

import javax.swing.*;
import java.awt.*;

public class ColorPanel extends JPanel {

	private Color color;

	public ColorPanel(Color color) {
		this.color = color;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

}
