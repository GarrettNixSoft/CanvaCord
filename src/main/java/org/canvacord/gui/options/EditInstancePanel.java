package org.canvacord.gui.options;

import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.User;
import org.canvacord.gui.options.page.*;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceConfiguration;
import org.canvacord.util.Globals;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;

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
		// TODO fill the data store with instance fields
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
		dataStore.store("class_reminder_schedule", instanceToEdit.getClassReminderSchedule());
		dataStore.store("do_meeting_markers", instanceToEdit.doMeetingMarkers());
		dataStore.store("command_availability", instanceToEdit.getAvailableCommands());
		dataStore.store("command_ids", instanceToEdit.getRegisteredCommands());
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

		System.out.println(dataStore.get("configured_roles"));

	}

	@Override
	protected void save() {

		// Write updated data store values to the instance config
		InstanceConfiguration instanceConfiguration = instanceToEdit.getConfiguration();

		boolean writeSuccess = instanceConfiguration.writeChanges();

		if (!writeSuccess) UserInput.showErrorMessage("An error occurred while saving the\nInstance configuration.", "Write Error");
		else System.out.println("Changes saved");

	}
}
