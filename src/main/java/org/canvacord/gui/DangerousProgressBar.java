package org.canvacord.gui;

import javax.swing.*;

public class DangerousProgressBar extends JProgressBar {

	private boolean failed;
	private DangerousProgressBarUI ui;

	public DangerousProgressBar() {
		ui = new DangerousProgressBarUI();
		setUI(ui);
	}

	public void setFailed(boolean failed) {
		ui.setFailed(failed);
	}
}
