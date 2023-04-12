package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordNotificationTarget;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.dialog.ClassScheduleDialog;
import org.canvacord.gui.dialog.CreateChannelDialog;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;
import org.canvacord.util.input.UserInput;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MeetingRemindersCard extends InstanceConfigCard {

	private List<ClassMeeting> classSchedule;
	private JSONObject reminderSchedule;

	private JCheckBox doMeetingReminders;
	private JCheckBox createRole;

	private JSpinner minutesSpinner;

	private JButton scanSyllabusButton;
	private JButton setManuallyButton;
	private JLabel classScheduleDisplay;

	private JComboBox<CanvaCordNotificationTarget> channelSelector;
	private JButton createChannelButton;
	private JButton refreshButton;

	// Discord data
	private Server targetServer;
	private List<CanvaCordNotificationTarget> availableChannels;

	// Elements that should be enabled/disabled based on checkbox status
	private List<JComponent> toggleComponents;

	public MeetingRemindersCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Meeting Reminders");
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

		final int cardLabelY = 4;
		final int cardLabelHeight = 70;

		final int meetingRemindersY = cardLabelY + cardLabelHeight;

		final int setScheduleLabelY = meetingRemindersY + 30;
		final int setScheduleRowY = setScheduleLabelY + 30;

		final int scheduleLabelY = setScheduleRowY + 30;
		final int scheduleBoxY = scheduleLabelY + 30;
		final int scheduleBoxWidth = 240;
		final int scheduleBoxHeight = 70;

		final int scheduleButtonX = componentX + scheduleBoxWidth + buttonSpacing;
		final int classScheduleButtonWidth = 110;
		final int buttonWidth = 80;
		final int classScheduleButtonHeight = 36;
		final int buttonHeight = 28;

		final int scheduleButtonY = scheduleBoxY + scheduleBoxHeight / 2 - classScheduleButtonHeight / 2;

		final int reminderChannelLabelY = scheduleBoxY + scheduleBoxHeight + 4;
		final int meetingChannelSelectorY = reminderChannelLabelY + 30;

		final int createButtonX = componentX + scheduleBoxWidth + buttonSpacing;
		final int refreshButtonX = createButtonX + buttonWidth + buttonSpacing;

		// Describe this card
		JLabel cardLabel = new JLabel(
				"""
					<html>You can configure CanvaCord to send reminders for when your class
					meets, as well as send pinned messages in a channel to mark the beginning
					and end of each class session. (This can help find messages relevant to a
					particular meeting.) Both of these features are optional.</html>"""
		);
		cardLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		cardLabel.setBounds(componentX, cardLabelY, WIDTH - componentX * 3, cardLabelHeight);
		contentPanel.add(cardLabel);

		// enable/disable meeting reminders
		doMeetingReminders = new JCheckBox("Send Class Meeting Reminders");
		doMeetingReminders.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		doMeetingReminders.setBounds(componentX, meetingRemindersY, 240, 28);
		contentPanel.add(doMeetingReminders);

		// enable/disable the reminder role
		createRole = new JCheckBox("Create Role for Reminders");
		createRole.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		createRole.setBounds(300, meetingRemindersY, 240, 28);
		contentPanel.add(createRole);

		// set reminder schedule
		JLabel setScheduleLabel = new JLabel("Set Reminder Schedule:");
		setScheduleLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		setScheduleLabel.setBounds(componentX, setScheduleLabelY, 200, 24);
		contentPanel.add(setScheduleLabel);

		JLabel preLabel = new JLabel("Send reminders");
		preLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		preLabel.setBounds(componentX, setScheduleRowY, 100, 24);
		contentPanel.add(preLabel);

		minutesSpinner = new JSpinner();
		minutesSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		minutesSpinner.setBounds(116, setScheduleRowY, 40, 24);
		minutesSpinner.setModel(new SpinnerNumberModel(15, 1, 180, 5));
		contentPanel.add(minutesSpinner);

		JLabel postLabel = new JLabel("minutes before each session");
		postLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		postLabel.setBounds(164, setScheduleRowY, 200, 24);
		contentPanel.add(postLabel);

		// set class schedule
		JLabel scheduleLabel = new JLabel("Class Schedule*:");
		scheduleLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		scheduleLabel.setBounds(componentX, scheduleLabelY, 120, 24);
		contentPanel.add(scheduleLabel);

		JLabel explanation = new JLabel("<html>*If you don't want Meeting Reminders, but you want to use Meeting " +
				"Markers on the next page, you should configure your class schedule here.</html>");
		explanation.setFont(CanvaCordFonts.LABEL_FONT_TINY);
		explanation.setBounds(componentX + 260, scheduleLabelY, 300, 36);
		contentPanel.add(explanation);

		classScheduleDisplay = new JLabel("Not Set");
		classScheduleDisplay.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		classScheduleDisplay.setBounds(componentX, scheduleBoxY, scheduleBoxWidth, scheduleBoxHeight);
		classScheduleDisplay.setVerticalAlignment(JLabel.TOP);
		classScheduleDisplay.setBorder(new LineBorder(Color.GRAY));
		contentPanel.add(classScheduleDisplay);

		scanSyllabusButton = new JButton("Scan Syllabus");
		scanSyllabusButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		scanSyllabusButton.setBounds(scheduleButtonX, scheduleButtonY, classScheduleButtonWidth, classScheduleButtonHeight);
		contentPanel.add(scanSyllabusButton);

		setManuallyButton = new JButton("Set Manually");
		setManuallyButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		setManuallyButton.setBounds(scheduleButtonX + classScheduleButtonWidth + buttonSpacing, scheduleButtonY, classScheduleButtonWidth, classScheduleButtonHeight);
		contentPanel.add(setManuallyButton);

		// set reminder channel
		JLabel reminderChannelLabel = new JLabel("Reminder Channel:");
		reminderChannelLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		reminderChannelLabel.setBounds(componentX, reminderChannelLabelY, 120, 24);
		contentPanel.add(reminderChannelLabel);

		channelSelector = new JComboBox<>();
		channelSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		channelSelector.setBounds(componentX, meetingChannelSelectorY, scheduleBoxWidth, buttonHeight);
		contentPanel.add(channelSelector);

		createChannelButton = new JButton("Create");
		createChannelButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		createChannelButton.setBounds(createButtonX, meetingChannelSelectorY, buttonWidth, buttonHeight);
		contentPanel.add(createChannelButton);

		refreshButton = new JButton("Refresh");
		refreshButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		refreshButton.setBounds(refreshButtonX, meetingChannelSelectorY, buttonWidth, buttonHeight);
		contentPanel.add(refreshButton);

		// collect components that should be toggled based on the main checkbox
		for (Component component : contentPanel.getComponents()) {
			if (component instanceof JComponent jComponent)
				if (!(jComponent instanceof JLabel))
					toggleComponents.add(jComponent);
		}

		// the main component should not toggle
		toggleComponents.remove(doMeetingReminders);

		// schedule buttons should not toggle
		toggleComponents.remove(scanSyllabusButton);
		toggleComponents.remove(setManuallyButton);

		// set initial state
		handleToggle();

	}

	@Override
	protected void initLogic() {

		// ================ MAIN TOGGLE ================
		doMeetingReminders.addChangeListener(event -> {
			handleToggle();
		});

		// ================ SCAN SYLLABUS ================
		scanSyllabusButton.addActionListener(event -> {
			UserInput.showWarningMessage("Not implemented yet. :/", "To Be Continued");
		});

		// ================ SET COURSE SCHEDULE MANUALLY ================
		setManuallyButton.addActionListener(event -> {
			List<ClassMeeting> newSchedule = ClassScheduleDialog.buildClassSchedule();
			if (!newSchedule.isEmpty()) {
				this.classSchedule = new ArrayList<>(newSchedule);
				describeClassSchedule();
			}
			checkScheduleSet();
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
	/**
	 * Prefills the MeetingReminderCard
	 * Andrew Bae
	 */
	public void prefillGUI(Instance instanceToEdit) {
		// TODO Andrew
		doMeetingReminders.setSelected(instanceToEdit.doMeetingReminders());
		createRole.setSelected(instanceToEdit.createRemindersRole());
		minutesSpinner.setValue(instanceToEdit.getConfiguration().getClassReminderSchedule());
		classSchedule = instanceToEdit.getClassSchedule();
		describeClassSchedule();
		refreshServers();
		//DiscordBot bot = DiscordBot.getBotInstance();
		//bot.login();
		long channelID = instanceToEdit.getConfiguration().getRawJSON().getLong("meeting_reminders_channel");
		System.out.println(channelID);
		try {
			ServerTextChannel ch = DiscordBot.getBotInstance().getApi().getServerTextChannelById(channelID).get();
			channelSelector.setSelectedItem(ch);
		} catch (Exception e) {
			System.out.println("Something Wrong");
		}
		//bot.disconnect();
	}

	private void handleToggle() {
		if (doMeetingReminders.isSelected()) {
			for (JComponent component : toggleComponents)
				component.setEnabled(true);
			checkScanSyllabusButton();
		}
		else {
			for (JComponent component : toggleComponents)
				component.setEnabled(false);
		}
		checkScheduleSet();
	}

	public void onNavigateTo() {
		checkScanSyllabusButton();
		// prefetch servers
		SwingUtilities.invokeLater(this::refreshServers);
	}

	private void checkScanSyllabusButton() {
		// TODO determine whether scan button should be enabled
	}

	private void checkScheduleSet() {
		if (doMeetingReminders.isSelected() && (classSchedule == null || classSchedule.isEmpty())) {
			getParentWizard().setNextButtonEnabled(false);
			getParentWizard().setNextButtonTooltip("<html>You must configure a class schedule<br>if meeting reminders are enabled.</html>");
		}
		else {
			getParentWizard().setNextButtonEnabled(true);
			getParentWizard().setNextButtonTooltip(null);
		}
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

	private void describeClassSchedule() {

		StringBuilder description = new StringBuilder();
		description.append("<html>");
		for (int i = 0; i < classSchedule.size(); i++) {
			ClassMeeting meeting = classSchedule.get(i);
			description.append(meeting.getWeekdayStr()).append(" @ ").append(meeting.getTimeDescription());
			if (i < classSchedule.size() - 1) description.append("<br/>");
		}
		description.append("</html>");

		classScheduleDisplay.setText(description.toString());

	}

	public boolean doMeetingReminders() {
		return doMeetingReminders.isSelected();
	}

	public boolean createRole() {
		return createRole.isSelected();
	}

	public int getReminderSchedule() {
		return (Integer) minutesSpinner.getValue();
	}

	public List<ClassMeeting> getClassSchedule() {
		return classSchedule;
	}

	public long getTargetChannelID() {
		return doMeetingReminders() ? ((CanvaCordNotificationTarget) channelSelector.getSelectedItem()).id() : -1;
	}

}
