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
		addCommandRecord("Assignments List", true);
		addCommandRecord("Assignments Search", true);
		addCommandRecord("Assignments Active", true);
		addCommandRecord("Assignment Details", true);
		addCommandRecord("Announcements List", true);
		addCommandRecord("Announcements Search", true);
		addCommandRecord("Announcement Details", true);
		addCommandRecord("Module List", true);
		addCommandRecord("Module Search", true);
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
		checkBoxMap.get("Assignments List").setSelected(commandAvailability.getBoolean("assignments_list"));
		checkBoxMap.get("Assignments Search").setSelected(commandAvailability.getBoolean("assignments_search"));
		checkBoxMap.get("Assignments Active").setSelected(commandAvailability.getBoolean("assignments_active"));
		checkBoxMap.get("Assignment Details").setSelected(commandAvailability.getBoolean("assignment_details"));
		checkBoxMap.get("Announcements List").setSelected(commandAvailability.getBoolean("announcements_list"));
		checkBoxMap.get("Announcements Search").setSelected(commandAvailability.getBoolean("announcements_search"));
		checkBoxMap.get("Announcement Details").setSelected(commandAvailability.getBoolean("announcement_details"));
		checkBoxMap.get("Module List").setSelected(commandAvailability.getBoolean("module_list"));
		checkBoxMap.get("Module Search").setSelected(commandAvailability.getBoolean("module_search"));
		checkBoxMap.get("Remind Me").setSelected(commandAvailability.getBoolean("remindme"));
	}

	@Override
	protected void verifyInputs() throws Exception {
		// TODO

		JSONObject commandAvailability = (JSONObject) dataStore.get("command_availability");
		commandAvailability.put("syllabus", checkBoxMap.get("Syllabus").isSelected());
		commandAvailability.put("textbooks", checkBoxMap.get("Textbooks").isSelected());
		commandAvailability.put("assignments_list", checkBoxMap.get("Assignments List").isSelected());
		commandAvailability.put("assignments_search", checkBoxMap.get("Assignments Search").isSelected());
		commandAvailability.put("assignments_active", checkBoxMap.get("Assignments Active").isSelected());
		commandAvailability.put("assignment_details", checkBoxMap.get("Assignment Details").isSelected());
		commandAvailability.put("announcements_list", checkBoxMap.get("Announcements List").isSelected());
		commandAvailability.put("announcements_search", checkBoxMap.get("Announcements Search").isSelected());
		commandAvailability.put("announcement_details", checkBoxMap.get("Announcement Details").isSelected());
		commandAvailability.put("module_list", checkBoxMap.get("Module List").isSelected());
		commandAvailability.put("module_search", checkBoxMap.get("Module Search").isSelected());
		commandAvailability.put("remindme", checkBoxMap.get("Remind Me").isSelected());

	}

	private void addCommandRecord(String name, boolean defaultState) {
		commands.add(new CommandRecord(name, defaultState));
	}
}
