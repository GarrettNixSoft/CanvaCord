package org.canvacord.gui.dialog;

import org.canvacord.discord.CanvaCordRole;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

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

	public RoleCreateDialog(CanvaCordRole roleToEdit) {
		this();
		prefillGUI(roleToEdit);
	}

	private void buildGUI() {
		// TODO
	}

	private void initLogic() {
		// TODO
	}

	private void prefillGUI(CanvaCordRole roleToEdit) {
		// TODO
	}

	private CanvaCordRole getResult() {
		return null;
	}

	public static Optional<CanvaCordRole> buildRole() {
		// TODO
		return Optional.empty();
	}

	public static Optional<CanvaCordRole> editRole(CanvaCordRole roleToEdit) {
		// TODO
		return Optional.empty();
	}

}
