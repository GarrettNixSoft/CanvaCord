package org.canvacord.gui.dialog;

import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordNotificationTarget;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.event.CanvaCordEvent;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.cards.instance.CourseAndServerCard;
import org.canvacord.setup.InstanceCreateWizard;
import org.canvacord.util.gui.DocumentSizeFilter;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.string.StringUtils;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.*;
import java.util.List;

public class NotificationCreateDialog extends CanvaCordDialog {

	private static final int WIDTH = 480;
	private static final int HEIGHT = 560;

	// Parent access for getting the server ID
	private InstanceCreateWizard parentWizard;

	// Roles available for selection
	private final List<CanvaCordRole> availableRoles;

	// Selecting roles to assign
	private JComboBox<CanvaCordRole> roleSelector;
	private JButton addButton;
	private JButton removeButton;
	private JTextField roleField;

	// Selecting the event type
	private JComboBox<CanvaCordEvent.Type> eventSelector;

	// Configuring the notification schedule
	private JTextField scheduleDescription;
	private JButton scheduleConfigureButton;
	private JSONObject scheduleObject;

	// Assigning a Discord channel for the notification
	private Server targetServer;
	private JComboBox<CanvaCordNotificationTarget> channelSelector;
	private JButton newChannelButton;
	private JButton refreshChannelsButton;

	// Customizing the message format
	private JTextArea messageArea;
	private JButton insertVariableButton;
	private JLabel charCountLabel;

	// field data
	private final Set<CanvaCordRole> selectedRoles;

	public NotificationCreateDialog(InstanceCreateWizard parentWizard, List<CanvaCordRole> availableRoles) {
		super("New Notification", WIDTH, HEIGHT);

		// assign the parent
		this.parentWizard = parentWizard;

		// roles available for assigning to notifications
		this.availableRoles = availableRoles;

		// collections
		selectedRoles = new HashSet<>();

		// build dialog
		buildGUI();
		buildLogic();

	}

	public NotificationCreateDialog(InstanceCreateWizard parentWizard, List<CanvaCordRole> availableRoles, CanvaCordNotification notificationToEdit) {
		this(parentWizard, availableRoles);
		prefillGUI(notificationToEdit);
	}

	@Override
	protected boolean verifyInputs() {

		if (roleField.getText().isBlank()) {
			UserInput.showErrorMessage("You must assign at least one role\nto the notification.", "No Roles Selected");
			return false;
		}

		return true;

	}

	private void buildGUI() {

		// ================ POSITIONING CONSTANTS ================
		// button size and spacing
		final int buttonWidth = 80;
		final int buttonHeight = 28;
		final int buttonSpacing = 10;

		// selector width
		final int selectorWidth = 240;

		// horizontal alignment
		final int componentX = 20;
		final int addButtonX = componentX + 240 + buttonSpacing;
		final int removeButtonX = addButtonX + buttonWidth + buttonSpacing;

		// role selection vertical positions
		final int roleLabelY = 4;
		final int roleSelectorY = roleLabelY + 30;
		final int roleFieldY = roleSelectorY + 36;

		// event selection vertical positions
		final int eventLabelY = roleFieldY + 32;
		final int eventSelectorY = eventLabelY + 30;

		// schedule config positions
		final int scheduleLabelY = eventSelectorY + 36;
		final int scheduleDescriptionY = scheduleLabelY + 30;
		final int scheduleDescriptionWidth = 310;
		final int scheduleConfigureButtonX = componentX + scheduleDescriptionWidth + buttonSpacing;

		// channel config positions
		final int channelLabelY = scheduleDescriptionY + 30;
		final int channelSelectorY = channelLabelY + 30;

		// message positions
		final int messageLabelY = channelSelectorY + 30;
		final int messageAreaY = messageLabelY + 30;

		// ================ LABEL ROLE SELECTION ================
		JLabel roleSelectLabel = new JLabel("Choose Roles:");
		roleSelectLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		roleSelectLabel.setBounds(componentX, roleLabelY, 240, 24);
		add(roleSelectLabel);

		// ================ SELECT ROLES FROM A DROP-DOWN MENU ================
		roleSelector = new JComboBox<>();
		roleSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		roleSelector.setBounds(componentX, roleSelectorY, selectorWidth, buttonHeight);

		// Add all available roles to the menu options
		for (CanvaCordRole role : availableRoles)
			roleSelector.addItem(role);

		add(roleSelector);

		// ================ ADD SELECTED ROLE ================
		addButton = new JButton("Add");
		addButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		addButton.setBounds(addButtonX, roleSelectorY, buttonWidth, buttonHeight);
		add(addButton);

		// ================ REMOVE SELECTED ROLE ================
		removeButton = new JButton("Remove");
		removeButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		removeButton.setBounds(removeButtonX, roleSelectorY, buttonWidth, buttonHeight);
		add(removeButton);

		// ================ SHOW SELECTED ROLES ================
		roleField = new JTextField(24);
		roleField.setEditable(false);
		roleField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		roleField.setBounds(componentX, roleFieldY, WIDTH - componentX * 3, 24);
		add(roleField);

		// ================ LABEL EVENT SELECTION ================
		JLabel eventSelectLabel = new JLabel("Select Event Type:");
		eventSelectLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		eventSelectLabel.setBounds(componentX, eventLabelY, 240, 24);
		add(eventSelectLabel);

		// ================ SELECT EVENT TYPE ================
		eventSelector = new JComboBox<>();
		eventSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		eventSelector.setBounds(componentX, eventSelectorY, selectorWidth, buttonHeight);

		// Add all relevant CanvaCordEvents
		for (CanvaCordEvent.Type eventType : CanvaCordEvent.NOTIFICATION_EVENTS) {
			eventSelector.addItem(eventType);
		}

		add(eventSelector);

		// ================ SCHEDULING THE NOTIFICATION ================
		JLabel scheduleLabel = new JLabel("Set Notification Schedule:");
		scheduleLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		scheduleLabel.setBounds(componentX, scheduleLabelY, 240, 24);
		add(scheduleLabel);

		scheduleDescription = new JTextField(24);
		scheduleDescription.setEditable(false);
		scheduleDescription.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		scheduleDescription.setBounds(componentX, scheduleDescriptionY, scheduleDescriptionWidth, buttonHeight);
		add(scheduleDescription);

		scheduleConfigureButton = new JButton("Configure");
		scheduleConfigureButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		scheduleConfigureButton.setBounds(scheduleConfigureButtonX, scheduleDescriptionY, buttonWidth + 20, buttonHeight);
		add(scheduleConfigureButton);

		// ================ ASSIGNING A CHANNEL FOR THE NOTIFICATION ================
		JLabel channelLabel = new JLabel("Assign a Discord Channel:");
		channelLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		channelLabel.setBounds(componentX, channelLabelY, 240, 24);
		add(channelLabel);

		channelSelector = new JComboBox<>();
		channelSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		channelSelector.setBounds(componentX, channelSelectorY, selectorWidth, buttonHeight);
		channelSelector.setEnabled(false); // START DISABLED
		add(channelSelector);

		SwingUtilities.invokeLater(() -> {
			refreshServers();
			channelSelector.setEnabled(true);
			channelSelector.revalidate();
			channelSelector.repaint();
		});

		// ================ ADDING A NEW CHANNEL ================
		newChannelButton = new JButton("Create");
		newChannelButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		newChannelButton.setBounds(addButtonX, channelSelectorY, buttonWidth, buttonHeight);
		add(newChannelButton);

		// ================ REFRESHING CHANNELS ================
		refreshChannelsButton = new JButton("Refresh");
		refreshChannelsButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		refreshChannelsButton.setBounds(removeButtonX, channelSelectorY, buttonWidth, buttonHeight);
		add(refreshChannelsButton);

		// ================ CUSTOMIZING THE MESSAGE ================
		JLabel messageLabel = new JLabel("Customize Message (Optional):");
		messageLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		messageLabel.setBounds(componentX, messageLabelY, 240, 24);
		add(messageLabel);

		messageArea = new JTextArea();
		messageArea.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		messageArea.setBounds(componentX, messageAreaY, WIDTH - componentX - buttonWidth - buttonSpacing * 2, 160);
		messageArea.setBorder(new LineBorder(Color.BLACK));
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);

		// limit the character size of the message
		AbstractDocument document = (AbstractDocument) messageArea.getDocument();
		document.setDocumentFilter(new DocumentSizeFilter(2000));

		add(messageArea);

		// ================ INSERTING VARIABLES ================
		insertVariableButton = new JButton("<html>Insert<br/>Variable</html>");
		insertVariableButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		insertVariableButton.setBounds(WIDTH - buttonSpacing - buttonWidth, messageAreaY, buttonWidth - buttonSpacing, buttonHeight * 2);
		add(insertVariableButton);

		// ================ SHOWING CHAR COUNT ================
		charCountLabel = new JLabel("0/2000 chars");
		charCountLabel.setFont(CanvaCordFonts.LABEL_FONT_TINY);
		charCountLabel.setBounds(WIDTH - buttonSpacing - buttonWidth - 4, messageAreaY + 130, buttonWidth, 36);
		add(charCountLabel);

	}

	private void buildLogic() {

		// ================ ADDING ROLE SELECTIONS ================
		addButton.addActionListener(event -> {
			CanvaCordRole selectedRole = (CanvaCordRole) roleSelector.getSelectedItem();
			if (selectedRoles.add(selectedRole)) {
				updateSelectedRolesField();
			}
		});

		// ================ REMOVING ROLE SELECTIONS ================
		removeButton.addActionListener(event -> {
			CanvaCordRole selectedRole = (CanvaCordRole) roleSelector.getSelectedItem();
			if (selectedRoles.remove(selectedRole)) {
				updateSelectedRolesField();
			}
		});

		// ================ REACTING TO EVENT SELECTIONS ================
		eventSelector.addItemListener(event -> {
			// Get the new selected item
			CanvaCordEvent.Type type = (CanvaCordEvent.Type) event.getItem();
			// If it's the due date approaching type
			if (type == CanvaCordEvent.Type.ASSIGNMENT_DUE_DATE_APPROACHING) {
				// And the current description indicates any other type
				if (scheduleDescription.getText().startsWith("Every")) {
					// Clear this schedule config, as it's not compatible with the selected event
					scheduleDescription.setText("");
					scheduleObject = null;
				}
			}
			// Otherwise (it's any other type)
			else {
				// If the current description indicates an assignment due date approaching type
				if (!scheduleDescription.getText().startsWith("Every")) {
					// Clear this schedule config, as it's not compatible with the selected event
					scheduleDescription.setText("");
					scheduleObject = null;
				}
			}
		});

		// ================ CONFIGURING SCHEDULES ================
		scheduleConfigureButton.addActionListener(event -> {
			CanvaCordEvent.Type eventType = (CanvaCordEvent.Type) eventSelector.getSelectedItem();
			NotificationScheduleDialog.buildNotificationSchedule(eventType).ifPresent(schedule -> {
				System.out.println("Built a schedule! Result: " + schedule);
				this.scheduleObject = schedule;
				describeSchedule();
			});
		});

		// ================ CREATING NEW CHANNELS ================
		newChannelButton.addActionListener(event -> {
			if (CreateChannelDialog.createNewChannel(targetServer).isPresent())
				refreshServers();
		});

		// ================ REFRESHING THE CHANNEL LIST ================
		refreshChannelsButton.addActionListener(event -> refreshServers());

		// ================ INSERTING VARIABLES INTO LIST ================
		insertVariableButton.addActionListener(event -> {
			if (eventSelector.getSelectedItem() == null) throw new CanvaCordException("Something exploded");
			switch ((CanvaCordEvent.Type) eventSelector.getSelectedItem()) {
				case ASSIGNMENT_DUE_DATE_APPROACHING, NEW_ASSIGNMENT -> {

					String[] options = {"Assignment Name", "Assignment Due Date", "Assignment Posted Date", "Point Value"};
					String variable = UserInput.getUserChoiceFromList("Choose a variable:", "Variable Selection", options);

					switch (variable) {
						case "Assignment Name" -> {
							messageArea.insert("${assignment.name}", messageArea.getCaretPosition());
						}
						case "Assignment Due Date" -> {
							messageArea.insert("${assignment.due}", messageArea.getCaretPosition());
						}
						case "Assignment Posted Date" -> {
							messageArea.insert("${assignment.date}", messageArea.getCaretPosition());
						}
						case "Point Value" -> {
							messageArea.insert("${assignment.points}", messageArea.getCaretPosition());
						}
					}

				}
				case ASSIGNMENT_DUE_DATE_CHANGED -> {

					String[] options = {"Assignment Name", "Assignment Due Date", "Assignment Posted Date", "Assignment Updated Date", "Point Value"};
					String variable = UserInput.getUserChoiceFromList("Choose a variable:", "Variable Selection", options);

					switch (variable) {
						case "Assignment Name" -> {
							messageArea.insert("${assignment.name}", messageArea.getCaretPosition());
						}
						case "Assignment Due Date" -> {
							messageArea.insert("${assignment.due}", messageArea.getCaretPosition());
						}
						case "Assignment Posted Date" -> {
							messageArea.insert("${assignment.date}", messageArea.getCaretPosition());
						}
						case "Assignment Updated Date" -> {
							messageArea.insert("${assignment.updated}", messageArea.getCaretPosition());
						}
						case "Point Value" -> {
							messageArea.insert("${assignment.points}", messageArea.getCaretPosition());
						}
					}

				}
				case NEW_ANNOUNCEMENT -> {

					String[] options = {"Announcement Title", "Announcement Message", "Announcement Posted Date"};
					String variable = UserInput.getUserChoiceFromList("Choose a variable:", "Variable Selection", options);

					switch (variable) {
						case "Announcement Title" -> {
							messageArea.insert("${announcement.title}", messageArea.getCaretPosition());
						}
						case "Announcement Message" -> {
							messageArea.insert("${announcement.message}", messageArea.getCaretPosition());
						}
						case "Announcement Posted Date" -> {
							messageArea.insert("${announcement.date}", messageArea.getCaretPosition());
						}

					}
				}
			}
		});

		// ================ REACTING TO THE SIZE OF THE DOCUMENT ================
		messageArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				int length = e.getDocument().getLength();
				charCountLabel.setText(length + "/2000 chars");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				int length = e.getDocument().getLength();
				charCountLabel.setText(length + "/2000 chars");
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				int length = e.getDocument().getLength();
				charCountLabel.setText(length + "/2000 chars");
			}
		});

	}

	private void prefillGUI(CanvaCordNotification notificationToEdit) {
		// TODO
	}

	private void refreshServers() {
		// disable selection during the refresh
		channelSelector.setEnabled(false);
		// get the server ID
		long serverID = ((CourseAndServerCard) parentWizard.getCard("course_server")).getServerID();
		// Fetch the server
		DiscordBot.getBotInstance().getServerByID(serverID).ifPresentOrElse(
				// If the fetch succeeded,
				server -> {
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

	private void updateSelectedRolesField() {
		// Special cases: empty and only one role
		if (selectedRoles.isEmpty())
			roleField.setText("");
		else if (selectedRoles.size() == 1)
			roleField.setText(selectedRoles.stream().toList().get(0).getName());
		// Order the roles
		List<CanvaCordRole> orderedRoles = new ArrayList<>(selectedRoles);
		// Build the text with a StringBuilder
		StringBuilder textBuilder = new StringBuilder();
		// All but the last role are comma-separated
		while (orderedRoles.size() > 1)
			textBuilder.append(orderedRoles.remove(0).getName()).append(", ");
		// The last role has no comma
		textBuilder.append(orderedRoles.remove(0).getName());
		// Set the field text
		roleField.setText(textBuilder.toString());
	}

	private void describeSchedule() {

		if (eventSelector.getSelectedItem() == null) {
			UserInput.showErrorMessage("Something exploded.", "What");
		}

		// TODO set the description field text here
		if (eventSelector.getSelectedItem().equals(CanvaCordEvent.Type.ASSIGNMENT_DUE_DATE_APPROACHING)) {
			int value = scheduleObject.getInt("value");
			String unit = scheduleObject.getString("unit");
			if (value == 1) unit = unit.substring(0, unit.length() - 1);
			scheduleDescription.setText(value + " " + unit + " before the due date");
		}
		else {
			String type = scheduleObject.getString("type");
			switch (type) {
				case "interval" -> {
					JSONObject interval = scheduleObject.getJSONObject("interval");
					int value = interval.getInt("value");
					String unit = interval.getString("unit");
					unit = unit.substring(0, unit.length() - 1);
					String format = String.format("Every %d %s", value, StringUtils.checkPlural(unit, value));
					scheduleDescription.setText(format);
				}
				case "daily" -> {
					JSONObject time = scheduleObject.getJSONObject("time");
					int hour = time.getInt("hour");
					int minute = time.getInt("minute");
					String ampm = time.getString("ampm").toUpperCase();
					String format = String.format("Every day at %2d:%02d%s", hour, minute, ampm);
					scheduleDescription.setText(format);
				}
				case "weekly" -> {
					StringBuilder format = new StringBuilder();
					format.append("Every ");
					JSONArray days = scheduleObject.getJSONArray("days");
					format.append(days.get(0));
					if (days.length() > 1) {
						for (int i = 1; i < days.length() - 1; i++) {
							format.append(", ");
							format.append(days.get(i));
						}
						format.append(" and ");
						format.append(days.get(days.length() - 1));
					}
					format.append(" at ");
					JSONObject time = scheduleObject.getJSONObject("time");
					int hour = time.getInt("hour");
					int minute = time.getInt("minute");
					String ampm = time.getString("ampm");
					String formattedTime = String.format("%2d:%02d%s", hour, minute, ampm);
					format.append(formattedTime);
					scheduleDescription.setText(format.toString());
				}
				default -> {
					scheduleDescription.setText("Invalid schedule");
				}
			}
		}

	}

	private Optional<CanvaCordNotification> getResult() {
		if (cancelled || !verifyInputs())
			return Optional.empty();
		else {
			// validate all selections
			if (roleSelector.getSelectedItem() == null) throw new CanvaCordException("Something blew up");
			if (eventSelector.getSelectedItem() == null) throw new CanvaCordException("Something exploded");
			if (channelSelector.getSelectedItem() == null) throw new CanvaCordException("Something imploded");
			// Fetch all the relevant user inputs
			long channelID = ((CanvaCordNotificationTarget) channelSelector.getSelectedItem()).id();
			CanvaCordEvent.Type eventType = (CanvaCordEvent.Type) eventSelector.getSelectedItem();
			List<CanvaCordRole> roleList = selectedRoles.stream().toList();
			String messageFormat = messageArea.getText();
			String friendlyScheduleDescription = scheduleDescription.getText();
			// Collect inputs into a neat object
			CanvaCordNotification result = new CanvaCordNotification(eventType, channelID, roleList,
					scheduleObject, messageFormat, friendlyScheduleDescription);
			// Send it back
			return Optional.of(result);
		}
	}

	public static Optional<CanvaCordNotification> buildNotification(InstanceCreateWizard parentWizard, List<CanvaCordRole> availableRoles) {
		NotificationCreateDialog dialog = new NotificationCreateDialog(parentWizard, availableRoles);
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

	public static Optional<CanvaCordNotification> editNotification(InstanceCreateWizard parentWizard, List<CanvaCordRole> availableRoles, CanvaCordNotification notification) {
		NotificationCreateDialog dialog = new NotificationCreateDialog(parentWizard, availableRoles, notification);
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

}
