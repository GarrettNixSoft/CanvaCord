package org.canvacord.gui.options;

import org.canvacord.gui.options.card.CourseServerPage;
import org.canvacord.instance.Instance;

public class EditInstancePanel extends OptionsPanel {

	private static final int WIDTH = 720;
	private static final int HEIGHT = 640;

	private final Instance instanceToEdit;

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
		addOptionPage(new CourseServerPage(instanceToEdit));
	}

	private void initLogic() {
		// TODO
	}

	private void populateDataStore() {
		// TODO fill the data store with instance fields
		dataStore.store("course_id", instanceToEdit.getCourseID());
		dataStore.store("server_id", instanceToEdit.getServerID());
	}

	@Override
	protected void complete(boolean success) {
		//
	}

}
