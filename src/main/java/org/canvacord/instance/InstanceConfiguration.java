package org.canvacord.instance;

import org.canvacord.util.file.FileUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public class InstanceConfiguration {

	public enum EndOfSemesterAction {
		DELETE, ARCHIVE
	}

	private final JSONObject configJSON;

	public InstanceConfiguration(JSONObject configJSON) {
		this.configJSON = configJSON;
	}

	// TODO: getters
	public boolean getGenerateExamEvents() {
		return configJSON.getBoolean("generate_exam_events");
	}

	public boolean getDoCustomReminders() {
		return configJSON.getBoolean("do_custom_reminders");
	}

	public EndOfSemesterAction getEndOfSemesterAction() {
		return switch (configJSON.getString("end_of_semester_action")) {
			case "delete" -> EndOfSemesterAction.DELETE;
			default -> EndOfSemesterAction.ARCHIVE;
		};
	}

	public JSONArray getInstanceRoles() {
		return configJSON.getJSONArray("roles");
	}

	public JSONArray getAssignmentDueReminders() {
		return configJSON.getJSONArray("assignment_due_reminders");
	}

	public static InstanceConfiguration defaultConfiguration() {
		File defaultConfigJSONFile = Paths.get("resources/default_config.json").toFile();
		Optional<JSONObject> defaultConfigJSON = FileUtil.getJSON(defaultConfigJSONFile);
		if (defaultConfigJSON.isPresent())
			return new InstanceConfiguration(defaultConfigJSON.get());
		else throw new RuntimeException("Default configuration is missing!");
	}

}
