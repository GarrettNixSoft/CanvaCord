package org.canvacord.gui.options;

import edu.ksu.canvas.model.Course;
import org.canvacord.gui.options.page.*;
import org.canvacord.instance.Instance;

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
		dataStore.store("course_id", instanceToEdit.getCourseID());
		dataStore.store("server_id", instanceToEdit.getServerID());
		dataStore.store("name", instanceToEdit.getName());
		dataStore.store("icon_path", instanceToEdit.getIconPath());
	}

	@Override
	protected void complete(boolean success) {
		//
	}
}
