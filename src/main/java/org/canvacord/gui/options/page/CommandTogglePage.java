package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.entity.CommandRecord;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandTogglePage extends OptionPage {

	private List<CommandRecord> commands;
	private List<JCheckBox> checkBoxes;
	private Map<String, JCheckBox> checkBoxMap;

	public CommandTogglePage() {
		super("Enable/Disable Commands");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("", "[]", "[]"));

		JLabel commandsLabel = new JLabel("Available Commands:");
		commandsLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(commandsLabel, "cell 0 0");

		// build list of commands that can be toggled
		commands = new ArrayList<>();
		addCommandRecord("Syllabus", true);
		addCommandRecord("Textbooks", true);
		addCommandRecord("Assignment", true);
		addCommandRecord("Announcement", true);
		addCommandRecord("Module", true);
		addCommandRecord("Remind Me", false);


		// store checkboxes to fetch their states later
		checkBoxes = new ArrayList<>();
		checkBoxMap = new HashMap<>();

		int rowsPerColumn = 10;

		// Add all command records!
		for (int i = 0; i < commands.size(); i++) {
			CommandRecord commandRecord = commands.get(i);
			JCheckBox checkBox = new JCheckBox(commandRecord.name());
			checkBox.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
			checkBox.setSelected(commandRecord.defaultState());
			add(checkBox, "cell " + (i / rowsPerColumn) + " " + (i % rowsPerColumn + 2));
			checkBoxes.add(checkBox);
			checkBoxMap.put(commandRecord.name(), checkBox);
		}

		// TODO
	}

	@Override
	protected void initLogic() {
		// TODO
	}

	@Override
	protected void prefillGUI() {
		commands = new ArrayList<>();
		JSONObject commandAvailability = (JSONObject) dataStore.get("command_availability");
		checkBoxMap.get("Syllabus").setSelected(commandAvailability.getBoolean("syllabus"));
		checkBoxMap.get("Textbooks").setSelected(commandAvailability.getBoolean("textbooks"));
		checkBoxMap.get("Assignment").setSelected(commandAvailability.getBoolean("assignment"));
		checkBoxMap.get("Announcement").setSelected(commandAvailability.getBoolean("announcement"));
		checkBoxMap.get("Module").setSelected(commandAvailability.getBoolean("module"));
		checkBoxMap.get("Remind Me").setSelected(commandAvailability.getBoolean("remindme"));
	}

	@Override
	protected void verifyInputs() throws Exception {
		// TODO

		JSONObject commandAvailability = (JSONObject) dataStore.get("command_availability");
		commandAvailability.put("syllabus", checkBoxMap.get("Syllabus").isSelected());
		commandAvailability.put("textbooks", checkBoxMap.get("Textbooks").isSelected());
		commandAvailability.put("assignment", checkBoxMap.get("Assignment").isSelected());
		commandAvailability.put("announcement", checkBoxMap.get("Announcement").isSelected());
		commandAvailability.put("module", checkBoxMap.get("Module").isSelected());
		commandAvailability.put("remindme", checkBoxMap.get("Remind Me").isSelected());

	}

	private void addCommandRecord(String name, boolean defaultState) {
		commands.add(new CommandRecord(name, defaultState));
	}
}
