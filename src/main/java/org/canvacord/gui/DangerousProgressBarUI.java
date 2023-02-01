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
		Graphics2D g2 = (Graphics2D) g.create();
		if (failed) {
			g2.setColor(Color.RED.darker());
			g2.fillRect(0, 0, component.getWidth(), component.getHeight());
		} else {
			JProgressBar progressBar = (JProgressBar) component;
			int progress = progressBar.getValue();
			int min = progressBar.getMinimum();
			int max = progressBar.getMaximum();
			double ratio = (double) progress / (max - min);
			int pixels = (int) (component.getWidth() * ratio);
			g2.setColor(Color.GREEN.darker().darker());
			g2.fillRect(0, 0, pixels, component.getHeight());
		}
		g2.dispose();
	}
}
