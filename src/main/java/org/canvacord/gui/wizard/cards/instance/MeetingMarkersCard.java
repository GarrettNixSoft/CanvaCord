package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordNotificationTarget;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.dialog.CreateChannelDialog;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;
import org.canvacord.util.input.UserInput;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class MeetingMarkersCard extends InstanceConfigCard {

	private JCheckBox doMeetingMarkers;
	private JCheckBox createRole;

	private JComboBox<CanvaCordNotificationTarget> channelSelector;
	private JButton createChannelButton;
	private JButton refreshButton;

	// Discord data
	private Server targetServer;
	private List<CanvaCordNotificationTarget> availableChannels;

	// Elements that should be enabled/disabled based on checkbox status
	private List<JComponent> toggleComponents;

	public MeetingMarkersCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Meeting Markers");
	}

	@Override
	protected void buildGUI() {

		// prepare collections
		availableChannels = new ArrayList<>();
		toggleComponents = new ArrayList<>();

		// Use an absolute layout for this one as well
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		// positioning
		final int componentX = 20;
		final int buttonSpacing = 20;

		final int buttonWidth = 80;
		final int buttonHeight = 28;

		final int toggleY = 94;
		final int markerChannelLabelY = toggleY + 36;
		final int markerChannelSelectorY = markerChannelLabelY + 30;

		final int createButtonX = componentX + 240 + buttonSpacing;
		final int refreshButtonX = createButtonX + buttonWidth + buttonSpacing;

		// Card label
		JLabel cardLabel = new JLabel(
				"""
					<html>Meeting markers are messages sent automatically at the start and end of each class session as
					configured on the previous page. They can be useful for indexing purposes, or to find messages
					relevant to a specific class session. You can choose a text channel for the markers to be send to,
					and whether the markers should be pinned.</html>
					"""
		);
		cardLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		cardLabel.setBounds(componentX, 4, WIDTH - componentX * 3, 80);
		contentPanel.add(cardLabel);

		// Toggle markers
		doMeetingMarkers = new JCheckBox("Send Class Meeting Markers");
		doMeetingMarkers.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		doMeetingMarkers.setBounds(componentX, toggleY, 240, 28);
		contentPanel.add(doMeetingMarkers);

		// Toggle create role
		createRole = new JCheckBox("Create Role for Markers");
		createRole.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		createRole.setBounds(300, toggleY, 240, 28);
		contentPanel.add(createRole);

		// set reminder channel
		JLabel markerChannelLabel = new JLabel("Marker Channel:");
		markerChannelLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		markerChannelLabel.setBounds(componentX, markerChannelLabelY, 120, 24);
		contentPanel.add(markerChannelLabel);

		channelSelector = new JComboBox<>();
		channelSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		channelSelector.setBounds(componentX, markerChannelSelectorY, 240, buttonHeight);
		contentPanel.add(channelSelector);

		createChannelButton = new JButton("Create");
		createChannelButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		createChannelButton.setBounds(createButtonX, markerChannelSelectorY, buttonWidth, buttonHeight);
		contentPanel.add(createChannelButton);

		refreshButton = new JButton("Refresh");
		refreshButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		refreshButton.setBounds(refreshButtonX, markerChannelSelectorY, buttonWidth, buttonHeight);
		contentPanel.add(refreshButton);

		// collect components that should be toggled based on the main checkbox
		for (Component component : contentPanel.getComponents()) {
			if (component instanceof JComponent jComponent)
				if (!(jComponent instanceof JLabel))
					toggleComponents.add(jComponent);
		}

		// the main component should not toggle
		toggleComponents.remove(doMeetingMarkers);

		// preemptively handle disabling components
		handleToggle();

		// TODO
	}

	@Override
	protected void initLogic() {

		// ================ MAIN TOGGLE ================
		doMeetingMarkers.addChangeListener(event -> {
			handleToggle();
		});

		// ================ CREATE DISCORD CHANNEL ================
		createChannelButton.addActionListener(event -> {
			if (CreateChannelDialog.createNewChannel(targetServer).isPresent())
				refreshServers();
		});

		// ================ REFRESH DISCORD CHANNELS ================
		refreshButton.addActionListener(event -> refreshServers());

	}

	@Override
	public void prefillGUI(Instance instanceToEdit) {
		// TODO Andrew
	}

	private void handleToggle() {
		if (doMeetingMarkers.isSelected()) {
			for (JComponent component : toggleComponents)
				component.setEnabled(true);
		}
		else {
			for (JComponent component : toggleComponents)
				component.setEnabled(false);
		}
	}

	public void onNavigateTo() {
		// prefetch servers
		SwingUtilities.invokeLater(this::refreshServers);
	}

	private void refreshServers() {
		// disable selection during the refresh
		channelSelector.setEnabled(false);
		// get the server ID
		long serverID = ((CourseAndServerCard) getParentWizard().getCard("course_server")).getServerID();
		// Fetch the server
		DiscordBot.getBotInstance().getServerByID(serverID).ifPresentOrElse(
				// If the fetch succeeded,
				server -> {
					// Clear the channel list
					availableChannels.clear();
					// Store the server reference
					targetServer = server;
					// Get channels and filter them to just text channels
					List<ServerChannel> channels = server.getChannels()
							.stream().filter(ch -> ch instanceof ServerTextChannel).toList();
					for (ServerChannel channel : channels) {
						// This should be safe because of the above filter
						ServerTextChannel textChannel = channel.asServerTextChannel().get();
						// Put them into the selector
						CanvaCordNotificationTarget target = new CanvaCordNotificationTarget(textChannel);
						availableChannels.add(target);
						channelSelector.addItem(target);
						channelSelector.setEnabled(true);
					}
				},
				// Otherwise if the fetch failed,
				() -> {
					// Warn the user and keep the selector disabled until a successful refresh occurs
					UserInput.showErrorMessage("Could not retrieve the target\nDiscord Server.", "Can't Reach Discord");
					channelSelector.setEnabled(false);
				}
		);
	}

	public boolean doMeetingMarkers() {
		return doMeetingMarkers.isSelected();
	}

	public long getTargetChannelID() {
		return doMeetingMarkers() ? ((CanvaCordNotificationTarget) channelSelector.getSelectedItem()).id() : -1;
	}

}
