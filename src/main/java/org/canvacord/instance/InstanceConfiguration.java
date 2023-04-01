package org.canvacord.instance;

import org.canvacord.canvas.TextbookInfo;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

	private JSONObject configJSON;

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
		// If no name was specified
		if (!configJSON.has("name"))
			configJSON.put("name", "instance_" + configJSON.getString("course_id") + "-" + configJSON.getLong("server_id"));
	}

	public void refresh() throws CanvaCordException {
		File configFile = CanvaCordPaths.getInstanceConfigPath(getCourseID(), getServerID()).toFile();
		Optional<JSONObject> readFromDisk = FileUtil.getJSON(configFile);
		if (readFromDisk.isPresent())
			configJSON = readFromDisk.get();
		else
			throw new CanvaCordException("Failed to refresh instance configuration");
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

	public String getInstanceName() {
		return configJSON.getString("name");
	}

	public String getCourseTitle() {
		return configJSON.getString("course_title");
	}

	public String getServerName() {
		return configJSON.getString("server_name");
	}

	public String getIconPath() {
		return configJSON.getString("icon_path");
	}

	public boolean hasSyllabus() { return configJSON.getBoolean("has_syllabus"); }

	public boolean doMeetingReminders() {
		return configJSON.getBoolean("do_meeting_reminders");
	}

	public boolean doMeetingMarkers() {
		return configJSON.getBoolean("do_meeting_markers");
	}

	public boolean createRemindersRole() {
		return configJSON.getBoolean("create_reminders_role");
	}

	public boolean createMarkersRole() {
		return configJSON.getBoolean("create_markers_role");
	}

	public boolean generateExamEvents() {
		return configJSON.getBoolean("generate_exam_events");
	}

	public boolean doCustomReminders() {
		return configJSON.getBoolean("do_custom_reminders");
	}

	public JSONObject getCommandAvailability() {
		return configJSON.getJSONObject("command_availability");
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

	public JSONArray getInstanceNotifications() {
		return configJSON.getJSONArray("notifications");
	}

	public List<CanvaCordRole> getConfiguredRoles() {
		List<CanvaCordRole> result = new ArrayList<>();
		JSONArray jsonRoles = getInstanceRoles();
		for (int i = 0; i < jsonRoles.length(); i++) {
			result.add(new CanvaCordRole(jsonRoles.getJSONObject(i)));
		}
		return result;
	}

	public List<CanvaCordNotification> getConfiguredNotifications() {
		List<CanvaCordNotification> result = new ArrayList<>();
		JSONArray jsonNotifications = getInstanceNotifications();
		for (int i = 0; i < jsonNotifications.length(); i++) {
			result.add(new CanvaCordNotification(jsonNotifications.getJSONObject(i), configJSON.getJSONArray("roles")));
		}
		return result;
	}

	public JSONObject getFetchSchedule() {
		return configJSON.getJSONObject("canvas_fetch_schedule");
	}

	public List<ClassMeeting> getClassSchedule() {
		JSONArray scheduleData = configJSON.getJSONArray("class_schedule");
		List<ClassMeeting> result = new ArrayList<>();
		for (int i = 0; i < scheduleData.length(); i++) {
			result.add(new ClassMeeting(scheduleData.getJSONObject(i)));
		}
		return result;
	}

	public int getClassReminderSchedule() {
		return configJSON.getInt("reminders_schedule");
	}

	public List<TextbookInfo> getTextbooks() {
		List<TextbookInfo> result = new ArrayList<>();
		JSONArray textbookData = configJSON.getJSONArray("textbook_files");
		for (int i = 0; i < textbookData.length(); i++) {
			result.add(new TextbookInfo(textbookData.getJSONObject(i)));
		}
		return result;
	}

	// ================================ UTILITY ================================
	public static InstanceConfiguration defaultConfiguration(String courseID, long serverID) {
		JSONObject defaultConfig = new JSONObject(defaultConfigJSON);
		defaultConfig.put("course_id", courseID);
		defaultConfig.put("server_id", serverID);
		return new InstanceConfiguration(defaultConfig);
	}

}
