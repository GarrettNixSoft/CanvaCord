package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordNotificationTarget;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.dialog.CreateChannelDialog;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.util.gui.ComponentUtils;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.time.LongTask;
import org.canvacord.util.time.LongTaskDialog;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MeetingMarkersPage extends OptionPage {

	private JCheckBox doMeetingMarkers;
	private JCheckBox createMarkersRole;

	private JComboBox<CanvaCordNotificationTarget> markerChannelSelector;

	private JButton createButton;
	private JButton refreshButton;

	public MeetingMarkersPage() {
		super("Meeting Markers");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void buildGUI() {

		setLayout(new MigLayout("", "[][][][][]", "[][][][][][][][]"));

		JLabel meetingMarkersLabel = new JLabel("Meeting Markers:");
		meetingMarkersLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(meetingMarkersLabel, "cell 0 0");

		doMeetingMarkers = new JCheckBox("Send Meeting Markers");
		doMeetingMarkers.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(doMeetingMarkers, "cell 0 2");

		createMarkersRole = new JCheckBox("Create Markers Role");
		createMarkersRole.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(createMarkersRole, "cell 0 4");

		JLabel channelSelectLabel = new JLabel("Send reminders to channel:");
		channelSelectLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(channelSelectLabel, "cell 0 6");

		markerChannelSelector = new JComboBox<>();
		markerChannelSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(markerChannelSelector, "cell 1 6 2 1, growx");

		createButton = new JButton("Create");
		createButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(createButton, "cell 1 7");

		refreshButton = new JButton("Refresh");
		refreshButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(refreshButton, "cell 2 7");

		setOnNavigateTo(() -> {
			if (((List<ClassMeeting>) dataStore.get("class_schedule")).isEmpty()) {
				ComponentUtils.setComponentsEnabledRecursively(this, false);
				doMeetingMarkers.setSelected(false);
				createMarkersRole.setSelected(false);
				doMeetingMarkers.setToolTipText("<html>You must configure a class schedule to<br/>enable meeting markers.</html>");
				createMarkersRole.setToolTipText("<html>You must configure a class schedule to<br/>enable meeting markers.</html>");
			}
			else {
				ComponentUtils.setComponentsEnabledRecursively(this, true);
				doMeetingMarkers.setToolTipText(null);
				createMarkersRole.setToolTipText(null);
			}

		});

	}

	@Override
	protected void initLogic() {

		doMeetingMarkers.addActionListener(event -> {
			createMarkersRole.setEnabled(doMeetingMarkers.isSelected());

			if (!doMeetingMarkers.isSelected())
				createMarkersRole.setSelected(false);
		});

		refreshButton.addActionListener(event -> {
			SwingUtilities.invokeLater(this::refreshChannels);
		});

		createButton.addActionListener(event -> {
			long serverID = (Long) dataStore.get("server_id");
			Server targetServer = DiscordBot.getBotInstance().getServerByID(serverID).get();
			if (CreateChannelDialog.createNewChannel(targetServer).isPresent())
				SwingUtilities.invokeLater(this::refreshChannels);
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void prefillGUI() {

		doMeetingMarkers.setSelected((Boolean) dataStore.get("do_meeting_markers"));
		createMarkersRole.setSelected((Boolean) dataStore.get("create_markers_role"));

		// only enable the second checkbox if the first is selected
		createMarkersRole.setEnabled(doMeetingMarkers.isSelected());


		List<CanvaCordNotificationTarget> availableChannels = (List<CanvaCordNotificationTarget>) dataStore.get("available_channels");

		for (CanvaCordNotificationTarget target : availableChannels) {
			markerChannelSelector.addItem(target);
			markerChannelSelector.setEnabled(true);
		}
		// get the markers channel
		long markersChannelID = (Long) dataStore.get("meeting_markers_channel");
		System.out.println("MARKERS CHANNEL ID: " + markersChannelID);
		// get the available channels
		for (CanvaCordNotificationTarget target : availableChannels) {
			if (target.id() == markersChannelID) {
				markerChannelSelector.setSelectedItem(target);
				System.out.println("matches channel: " + target.serverChannel().getName());
				break;
			}
		}

	}

	@Override
	protected void verifyInputs() throws Exception {

		// No errors possible here
		dataStore.store("do_meeting_markers", doMeetingMarkers.isSelected());
		dataStore.store("create_markers_role", createMarkersRole.isSelected());

		// validate channel selection
		if (markerChannelSelector.getSelectedItem() != null) {
			CanvaCordNotificationTarget reminderTarget = (CanvaCordNotificationTarget) markerChannelSelector.getSelectedItem();
			// write selection to the data store
			dataStore.store("meeting_markers_channel", reminderTarget.id());
		}

	}

	private void refreshChannels() {
		LongTask refreshTask = () -> {
			// disable selection during the refresh
			markerChannelSelector.setEnabled(false);
			// Fetch the server ID
			long serverID = (Long) dataStore.get("server_id");
			// Fetch the server
			DiscordBot.getBotInstance().getServerByID(serverID).ifPresentOrElse(
					// If the fetch succeeded,
					server -> {
						// Clear the channel list
						List<CanvaCordNotificationTarget> availableChannels = new ArrayList<>();
						// Get channels and filter them to just text channels
						List<ServerChannel> channels = server.getChannels()
								.stream().filter(ch -> ch instanceof ServerTextChannel).toList();
						for (ServerChannel channel : channels) {
							// This should be safe because of the above filter
							ServerTextChannel textChannel = channel.asServerTextChannel().get();
							// Put them into the selector
							CanvaCordNotificationTarget target = new CanvaCordNotificationTarget(textChannel);
							availableChannels.add(target);
							markerChannelSelector.addItem(target);
							markerChannelSelector.setEnabled(true);
						}
						// store the channels for other pages to use
						dataStore.store("available_channels", availableChannels);
					},
					// Otherwise if the fetch failed,
					() -> {
						// Warn the user and keep the selector disabled until a successful refresh occurs
						UserInput.showErrorMessage("Could not retrieve the target\nDiscord Server.", "Can't Reach Discord");
						markerChannelSelector.setEnabled(false);
					}
			);
		};
		LongTaskDialog.runLongTask(refreshTask, "Loading Discord channels...", "Fetch");
	}

}
