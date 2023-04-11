package org.canvacord.gui.dialog;

import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.util.input.UserInput;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;

import javax.swing.*;
import java.util.Optional;

public class CreateChannelDialog extends CanvaCordDialog {

	private static final int WIDTH = 240;
	private static final int HEIGHT = 140;

	private final Server targetServer;

	private JTextField nameField;

	public CreateChannelDialog(Server targetServer) {
		super("Create Channel", WIDTH, HEIGHT);
		this.targetServer = targetServer;
		buildGUI();
	}

	@Override
	protected boolean verifyInputs() {
		// Get the user's input
		String name = nameField.getText();
		// It cannot be blank
		if (name.isBlank()) {
			UserInput.showErrorMessage("Channel name cannot be blank.", "Invalid Name");
			return false;
		}
		// It can only contain alphanumeric characters and hyphens
		boolean containsUppers = false;
		for (char c : name.toCharArray()) {
			// If there are any non-alphanumeric, non-hyphen characters, this is inavlid
			if (!(Character.isAlphabetic(c) || Character.isDigit(c) || c == '-')) {
				UserInput.showErrorMessage("Channel names can only contain alphanumeric\ncharacters and hyphens (-).", "Invalid Name");
				return false;
			}
			// If there are any uppercase letters, warn the user once
			if (!containsUppers && Character.isUpperCase(c)) {
				containsUppers = true;
			}
		}
		if (containsUppers) {
			// Warn the user about uppercase on Discord and ask if they want to keep the name as-is
			String[] options = {"Keep", "Change"};
			if (UserInput.askToConfirmCustom("The name you have entered contains uppercase characters.\n" +
												"Please note that on Discord these will be replaced with their\n" +
												"lowercase counterparts. Would you like keep or change the name?",
											"Uppercase Warning", options, 0, JOptionPane.WARNING_MESSAGE) == 0) {
				// pre-lowercase the text
				nameField.setText(name.toLowerCase());
				return true;
			}
		}
		return true;
	}

	private void buildGUI() {

		JLabel inputLabel = new JLabel("Enter a name:");
		inputLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		inputLabel.setBounds(20, 4, 200, 24);
		add(inputLabel);

		nameField = new JTextField(16);
		nameField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		nameField.setBounds(20, 30, 200, 24);
		add(nameField);

	}

	private Optional<ServerTextChannel> getResult() {
		if (cancelled || !verifyInputs())
			return Optional.empty();
		else {
			try {
				ServerTextChannel result = targetServer.createTextChannelBuilder()
						.setName(nameField.getText())
						.create().join();
				return Optional.of(result);
			}
			catch (Exception e) {
				UserInput.showExceptionWarning(e);
				e.printStackTrace();
				return Optional.empty();
			}
		}
	}

	public static Optional<ServerTextChannel> createNewChannel(Server targetServer) {
		CreateChannelDialog dialog = new CreateChannelDialog(targetServer);
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

}
