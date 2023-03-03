package org.canvacord.gui.dialog;

import org.canvacord.discord.CanvaCordRole;

import javax.swing.*;
import java.awt.*;

public class RoleCreateDialog extends JDialog {

	private static final int WIDTH = 400;
	private static final int HEIGHT = 300;

	public RoleCreateDialog() {
		super();
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setResizable(false);

		buildGUI();
		initLogic();
	}

	private void buildGUI() {
		// TODO
	}

	private void initLogic() {
		// TODO
	}

	public CanvaCordRole getResult() {
		return null;
	}

}
