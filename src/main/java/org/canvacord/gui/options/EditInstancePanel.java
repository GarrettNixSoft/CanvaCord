package org.canvacord.gui.options;

import edu.ksu.canvas.model.Course;
import org.canvacord.gui.options.page.*;
import org.canvacord.instance.Instance;
import org.canvacord.util.Globals;

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
		//
	}
}
