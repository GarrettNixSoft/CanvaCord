package org.canvacord.gui.component;

import javax.swing.*;

public class DangerousProgressBar extends JProgressBar {

	private boolean failed;
	private DangerousProgressBarUI ui;

	public DangerousProgressBar() {
		ui = new DangerousProgressBarUI();
		setUI(ui);
	}

	public DangerousProgressBar(int min, int max) {
		super(min, max);
		ui = new DangerousProgressBarUI();
		setUI(ui);
	}

	public void setFailed(boolean failed) {
		ui.setFailed(failed);
	}
}
