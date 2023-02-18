package org.canvacord.instance;

import org.canvacord.util.file.FileUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public class InstanceConfiguration {

	private static JSONObject defaultConfigJSON;

	static {
		File defaultConfigJSONFile = Paths.get("resources/default_config.json").toFile();
		Optional<JSONObject> defaultConfigJSON = FileUtil.getJSON(defaultConfigJSONFile);
		if (defaultConfigJSON.isPresent()) {
			InstanceConfiguration.defaultConfigJSON = defaultConfigJSON.get();
		}
		else throw new RuntimeException("Default configuration is missing!");
	}

	public enum EndOfSemesterAction {
		DELETE, ARCHIVE
	}

	private final JSONObject configJSON;

	public InstanceConfiguration(JSONObject configJSON) {
		this.configJSON = configJSON;
		initDefaults();
	}

	/**
	 * Any values not set in the JSONObject passed to the constructor
	 * will have default values inserted here. For example if no fetch
	 * schedule for polling Canvas data is set, the default schedule
	 * will be applied.
	 */
	private void initDefaults() {
		for (String key : defaultConfigJSON.keySet()) {
			if (!configJSON.has(key))
				configJSON.put(key, defaultConfigJSON.get(key));
		}
	}

	// TODO: getters


	public JSONObject getRawJSON() {
		return configJSON;
	}

	public String getCourseID() {
		return configJSON.getString("course_id");
	}

	public long getServerID() {
		return configJSON.getLong("server_id");
	}

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
		return new InstanceConfiguration(defaultConfigJSON);
	}

}
