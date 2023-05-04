package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordNotificationTarget;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.dialog.CreateChannelDialog;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.util.CanvaCordModels;
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

public class MeetingRemindersPage extends OptionPage {

	private JCheckBox doMeetingReminders;
	private JCheckBox createRemindersRole;
	private JSpinner reminderScheduleSpinner;

	private JComboBox<CanvaCordNotificationTarget> reminderChannelSelector;

	private JButton createButton;
	private JButton refreshButton;

	public MeetingRemindersPage() {
		super("Meeting Reminders");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void buildGUI() {

		setLayout(new MigLayout("", "[grow]", "[][][][][][][][][]"));

		JLabel remindersLabel = new JLabel("Meeting Reminders:");
		remindersLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(remindersLabel, "cell 0 0");

		doMeetingReminders = new JCheckBox("Send Meeting Reminders");
		doMeetingReminders.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(doMeetingReminders, "cell 0 2");

		createRemindersRole = new JCheckBox("Create Reminders Role");
		createRemindersRole.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(createRemindersRole, "cell 0 4");

		JPanel schedulePanel = new JPanel();
		schedulePanel.setLayout(new MigLayout("", "[grow][][grow]", "[]"));
		add(schedulePanel, "cell 0 6 4 1");

		JLabel preScheduleLabel = new JLabel("Send reminders: ");
		preScheduleLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		schedulePanel.add(preScheduleLabel, "cell 0 0");

		reminderScheduleSpinner = new JSpinner();
		reminderScheduleSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		reminderScheduleSpinner.setModel(CanvaCordModels.getGenericNumberModel(5));
		schedulePanel.add(reminderScheduleSpinner, "cell 1 0, width 40px!");

		JLabel postScheduleLabel = new JLabel(" minutes before each meeting");
		postScheduleLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		schedulePanel.add(postScheduleLabel, "cell 2 0");

		JLabel channelSelectLabel = new JLabel("Send reminders to channel:");
		channelSelectLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(channelSelectLabel, "cell 0 8");

		reminderChannelSelector = new JComboBox<>();
		reminderChannelSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(reminderChannelSelector, "cell 1 8 2 1, growx");

		createButton = new JButton("Create");
		createButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(createButton, "cell 1 9");

		refreshButton = new JButton("Refresh");
		refreshButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(refreshButton, "cell 2 9");

		setOnNavigateTo(() -> {
			if (((List<ClassMeeting>) dataStore.get("class_schedule")).isEmpty()) {
				ComponentUtils.setComponentsEnabledRecursively(this, false);
				doMeetingReminders.setSelected(false);
				createRemindersRole.setSelected(false);
				doMeetingReminders.setToolTipText("<html>You must configure a class schedule to<br/>enable meeting reminders.</html>");
				createRemindersRole.setToolTipText("<html>You must configure a class schedule to<br/>enable meeting reminders.</html>");
				reminderScheduleSpinner.setToolTipText("<html>You must configure a class schedule to<br/>enable meeting reminders.</html>");
			}
			else {
				ComponentUtils.setComponentsEnabledRecursively(this, true);
				doMeetingReminders.setToolTipText(null);
				createRemindersRole.setToolTipText(null);
				reminderScheduleSpinner.setToolTipText(null);
			}
		});

	}

	@Override
	protected void initLogic() {

		doMeetingReminders.addActionListener(event -> {
			createRemindersRole.setEnabled(doMeetingReminders.isSelected());
			reminderScheduleSpinner.setEnabled(doMeetingReminders.isSelected());

			if (!doMeetingReminders.isSelected())
				createRemindersRole.setSelected(false);
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
	protected void prefillGUI() {
		doMeetingReminders.setSelected((Boolean) dataStore.get("do_meeting_reminders"));
		createRemindersRole.setSelected((Boolean) dataStore.get("create_reminders_role"));
		int reminderSchedule = (Integer) dataStore.get("class_reminder_schedule");
		reminderScheduleSpinner.setValue(reminderSchedule);

		List<CanvaCordNotificationTarget> availableChannels = (List<CanvaCordNotificationTarget>) dataStore.get("available_channels");

		for (CanvaCordNotificationTarget target : availableChannels) {
			reminderChannelSelector.addItem(target);
			reminderChannelSelector.setEnabled(true);
		}
		// get the markers channel
		long markersChannelID = (Long) dataStore.get("meeting_reminders_channel");
		System.out.println("REMINDERS CHANNEL ID: " + markersChannelID);
		// get the available channels
		for (CanvaCordNotificationTarget target : availableChannels) {
			if (target.id() == markersChannelID) {
				reminderChannelSelector.setSelectedItem(target);
				System.out.println("matches channel: " + target.serverChannel().getName());
				break;
			}
		}

	}

	@Override
	protected void verifyInputs() throws Exception {
		dataStore.store("do_meeting_reminders", doMeetingReminders.isSelected());
		dataStore.store("create_reminders_role", createRemindersRole.isSelected());
		dataStore.store("class_reminders_schedule", reminderScheduleSpinner.getValue());

		// validate channel selection
		if (reminderChannelSelector.getSelectedItem() != null) {
			CanvaCordNotificationTarget reminderTarget = (CanvaCordNotificationTarget) reminderChannelSelector.getSelectedItem();
			// write selection to the data store
			dataStore.store("meeting_reminders_channel", reminderTarget.id());
		}
	}

	private void refreshChannels() {
		LongTask refreshTask = () -> {
			// disable selection during the refresh
			reminderChannelSelector.setEnabled(false);
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
							reminderChannelSelector.addItem(target);
							reminderChannelSelector.setEnabled(true);
						}
						// store the channels for other pages to use
						dataStore.store("available_channels", availableChannels);
					},
					// Otherwise if the fetch failed,
					() -> {
						// Warn the user and keep the selector disabled until a successful refresh occurs
						UserInput.showErrorMessage("Could not retrieve the target\nDiscord Server.", "Can't Reach Discord");
						reminderChannelSelector.setEnabled(false);
					}
			);
		};
		LongTaskDialog.runLongTask(refreshTask, "Loading Discord channels...", "Fetch");
	}

}
