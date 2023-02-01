package org.canvacord.gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class DangerousProgressBarUI extends BasicProgressBarUI {

	private boolean failed;

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	@Override
	protected void paintDeterminate(Graphics g, JComponent component) {
		if (failed) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.RED.darker());
			g2.fillRect(0, 0, component.getWidth(), component.getHeight());
			System.out.println("Drew red rect @ (" + component.getX() + ", " + component.getY() + "), size " + component.getWidth() + "x" + component.getHeight());
			g2.dispose();
		} else {
			super.paintDeterminate(g, component);
			System.out.println("Drew normally @ (" + component.getX() + ", " + component.getY() + "), size " + component.getWidth() + "x" + component.getHeight());
		}
	}
}
