package org.canvacord.gui.options;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.GuiAction;
import org.canvacord.gui.GuiDataStore;

import javax.swing.*;

public abstract class OptionPage extends JPanel {

	private final String name;

	protected GuiDataStore dataStore;

	private GuiAction onNavigateTo;
	private GuiAction onNavigateAway;

	public OptionPage(String name) {
		this.name = name;
		buildGUI();
		initLogic();
	}

	// ================ BUILD ================
	protected abstract void buildGUI();
	protected abstract void initLogic();

	// ================ SETUP ================
	protected void provideDataStore(GuiDataStore dataStore) {
		this.dataStore = dataStore;
	}

	protected abstract void prefillGUI();

	public void setOnNavigateTo(GuiAction onNavigateTo) {
		this.onNavigateTo = onNavigateTo;
	}

	public void setOnNavigateAway(GuiAction onNavigateAway) {
		this.onNavigateAway = onNavigateAway;
	}

	// ================ GETTERS ================
	public String getName() {
		return name;
	}

	// ================ VALIDATION ================
	protected abstract void verifyInputs() throws CanvaCordException;

	// ================ ACTIONS ================
	public void onNavigateTo() {
		if (onNavigateTo != null) onNavigateTo.execute();
	}

	public void onNavigateAway() {
		if (onNavigateAway != null) onNavigateAway.execute();
	}

}
