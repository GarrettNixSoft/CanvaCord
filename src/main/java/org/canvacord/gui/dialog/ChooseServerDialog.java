package org.canvacord.gui.dialog;

import edu.ksu.canvas.model.Course;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CourseWrapper;
import org.canvacord.entity.ServerWrapper;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.persist.ConfigManager;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.time.LongTask;
import org.canvacord.util.time.LongTaskDialog;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChooseServerDialog extends CanvaCordDialog {

	private static final int WIDTH = 400;
	private static final int HEIGHT = 150;

	private static final List<Server> discordServers = new ArrayList<>();

	private JComboBox<ServerWrapper> serverSelector;

	public ChooseServerDialog() {
		super("Choose Server", WIDTH, HEIGHT);
		buildGUI();
	}

	@Override
	protected boolean verifyInputs() {

		if (serverSelector.getItemCount() == 0) {
			UserInput.showErrorMessage("Failed to load Discord servers.\nPlease check your Discord bot\nand access token.", "Access Error");
			return false;
		}
		if (serverSelector.getSelectedItem() == null) {
			UserInput.showErrorMessage("You must select a server.", "No Selection");
			return false;
		}

		return true;

	}

	private void buildGUI() {

		final int componentX = 20;
		final int labelY = 4;
		final int selectorY = 36;

		JLabel label = new JLabel("Choose a Discord Server:");
		label.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		label.setBounds(componentX, labelY, WIDTH - componentX * 3, 28);
		add(label);

		serverSelector = new JComboBox<>();
		serverSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		serverSelector.setBounds(componentX, selectorY, WIDTH - componentX * 3, 28);
		add(serverSelector);

		pack();

		SwingUtilities.invokeLater(() -> {
			if (discordServers.isEmpty()) {
				LongTask fetchServers = () -> discordServers.addAll(DiscordBot.getBotInstance().getServerMemberships());
				LongTaskDialog.runLongTask(fetchServers, "Fetching Discord Servers...", "Fetch");
			}
			if (discordServers.isEmpty()) {
				UserInput.showWarningMessage("Failed to load Discord servers.\nPlease check your Discord bot\nand access token.", "Access Error");
			}
			else for (Server server : discordServers) {
				serverSelector.addItem(new ServerWrapper(server));
			}
		});

	}

	public Optional<Server> getResult() {
		if (cancelled)
			return Optional.empty();
		else {
			return Optional.of(((ServerWrapper) serverSelector.getSelectedItem()).server());
		}
	}

	public static Optional<Server> chooseServer() {
		ChooseServerDialog dialog = new ChooseServerDialog();
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

}
