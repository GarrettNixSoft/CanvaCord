package org.canvacord.gui.options;

import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.User;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordNotificationTarget;
import org.canvacord.gui.options.page.*;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceConfiguration;
import org.canvacord.util.Globals;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.time.LongTask;
import org.canvacord.util.time.LongTaskDialog;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditInstancePanel extends OptionsPanel {

	private static final int WIDTH = 720;
	private static final int HEIGHT = 640;

	private final Instance instanceToEdit;

	private ResourcesPage resourcesPage;
	private MeetingsPage meetingsPage;
	private CommandInfoPage commandInfoPage;

	public EditInstancePanel(Instance instanceToEdit) {
		super("Edit " + instanceToEdit.getName(), WIDTH, HEIGHT);
		this.instanceToEdit = instanceToEdit;
		Globals.EDIT_INSTANCE_ID = instanceToEdit.getInstanceID();
		buildGUI();
		initLogic();
		populateDataStore();
		provideDataStore();
		prefillGUI();
		selectFirstPage();
	}

	private void buildGUI() {
		addOptionPage(new CourseServerPage());
		addOptionPage(new NameIconPage());
		addOptionPage(new CanvasFetchPage());
		addOptionPage(new RolesPage());
		addOptionPage(new NotificationsPage());
		addOptionPage(resourcesPage = new ResourcesPage());
		addOptionPage(resourcesPage, new SyllabusPage());
		addOptionPage(resourcesPage, new TextbooksPage());
		addOptionPage(meetingsPage = new MeetingsPage());
		addOptionPage(meetingsPage, new CourseSchedulePage());
		addOptionPage(meetingsPage, new MeetingRemindersPage());
		addOptionPage(meetingsPage, new MeetingMarkersPage());
		addOptionPage(commandInfoPage = new CommandInfoPage());
		addOptionPage(commandInfoPage, new CommandTogglePage());
		addOptionPage(commandInfoPage, new CommandOptionsPage());
	}

	private void initLogic() {
		// TODO
	}

	private void populateDataStore() {
		dataStore.store("instance", instanceToEdit);
		dataStore.store("course_id", instanceToEdit.getCourseID());
		dataStore.store("server_id", instanceToEdit.getServerID());
		dataStore.store("name", instanceToEdit.getName());
		dataStore.store("icon_path", instanceToEdit.getIconPath());
		dataStore.store("fetch_schedule", instanceToEdit.getCanvasFetchSchedule());
		dataStore.store("roles", instanceToEdit.getConfiguredRoles());
		dataStore.store("registered_roles", instanceToEdit.getRegisteredRoles());
		dataStore.store("notifications", instanceToEdit.getConfiguredNotifications());
		dataStore.store("has_syllabus", instanceToEdit.hasSyllabus());
		dataStore.store("textbooks", instanceToEdit.getTextbooks());
		dataStore.store("class_schedule", instanceToEdit.getClassSchedule());
		dataStore.store("do_meeting_reminders", instanceToEdit.doMeetingReminders());
		dataStore.store("create_reminders_role", instanceToEdit.createRemindersRole());
		dataStore.store("class_reminder_schedule", instanceToEdit.getClassReminderSchedule());
		dataStore.store("meeting_reminders_channel", instanceToEdit.getMeetingRemindersChannel());
		dataStore.store("do_meeting_markers", instanceToEdit.doMeetingMarkers());
		dataStore.store("create_markers_role", instanceToEdit.createMarkersRole());
		dataStore.store("meeting_markers_channel", instanceToEdit.getMeetingMarkersChannel());
		dataStore.store("command_availability", instanceToEdit.getCommandAvailability());
		dataStore.store("command_ids", instanceToEdit.getRegisteredCommands());
		refreshChannels();
	}

	@Override
	protected void complete(boolean success) {

		// Don't write any changes if the editor failed
		if (!success) return;

		// Write updated data store values to the instance config
		InstanceConfiguration instanceConfiguration = instanceToEdit.getConfiguration();

		// TODO
		instanceConfiguration.setInstanceName((String) dataStore.get("name"));
		instanceConfiguration.setIconPath((String) dataStore.get("icon_path"));
		instanceConfiguration.setFetchSchedule((JSONObject) dataStore.get("fetch_schedule"));
		instanceConfiguration.setConfiguredRoles((JSONArray) dataStore.get("configured_roles"));
		instanceConfiguration.setConfiguredNotifications((JSONArray) dataStore.get("configured_notifications"));
		instanceConfiguration.setTextbooks((JSONArray) dataStore.get("textbooks"));
		instanceConfiguration.setDoMeetingReminders((Boolean) dataStore.get("do_meeting_reminders"));
		instanceConfiguration.setCreateRemindersRole((Boolean) dataStore.get("create_reminders_role"));
		instanceConfiguration.setClassRemindersSchedule((Integer) dataStore.get("class_reminders_schedule"));
		instanceConfiguration.setClassRemindersChannel((Long) dataStore.get("meeting_reminders_channel"));
		instanceConfiguration.setDoMeetingMarkers((Boolean) dataStore.get("do_meeting_markers"));
		instanceConfiguration.setCreateMarkersRole((Boolean) dataStore.get("create_markers_role"));
		instanceConfiguration.setClassMarkersChannel((Long) dataStore.get("meeting_markers_channel"));
		instanceConfiguration.setCommandAvailability((JSONObject) dataStore.get("command_availability"));

	}

	@Override
	protected void save() {

		// Write updated data store values to the instance config
		InstanceConfiguration instanceConfiguration = instanceToEdit.getConfiguration();

		boolean writeSuccess = instanceConfiguration.writeChanges();

		if (!writeSuccess) UserInput.showErrorMessage("An error occurred while saving the\nInstance configuration.", "Write Error");
		else System.out.println("Changes saved");

	}

	private void refreshChannels() {
		LongTask refreshTask = () -> {
			// Fetch the server ID
			long serverID = (Long) dataStore.get("server_id");
			// Fetch the server
			DiscordBot.getBotInstance().getServerByID(serverID).ifPresent(
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
						}
						// store the channels for other pages to use
						dataStore.store("available_channels", availableChannels);
					}
			);
		};
		LongTaskDialog.runLongTask(refreshTask, "Loading Discord channels...", "Fetch");
	}


}
