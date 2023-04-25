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

	@Override
	public String toString() {
		return name;
	}

	// ================ VALIDATION ================

	/**
	 * Validate all inputs on this page. If all inputs are valid,
	 * they will be stored into the GuiDataStore so that they can be
	 * written to disk if all pages pass this check.
	 * @throws CanvaCordException if any inputs on this page are invalid
	 */
	protected abstract void verifyInputs() throws Exception;

	// ================ ACTIONS ================
	public void onNavigateTo() {
		if (onNavigateTo != null) onNavigateTo.execute();
	}

	public void onNavigateAway() {
		if (onNavigateAway != null) onNavigateAway.execute();
	}

}
