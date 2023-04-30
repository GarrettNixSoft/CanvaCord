package org.canvacord.instance;

import org.canvacord.canvas.TextbookInfo;
import org.canvacord.discord.commands.Command;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.util.compare.ListComparator;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

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

	// ================ CACHED VALUES ================
	private final List<CanvaCordRole> configuredRoles = new ArrayList<>();
	private final List<CanvaCordRole> registeredRoles = new ArrayList<>();
	private final List<CanvaCordNotification> configuredNotifications = new ArrayList<>();
	private final Map<String, Boolean> availableCommands = new HashMap<>();
	private final Map<Long, Class<? extends Command>> registeredCommands = new HashMap<>();

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

	public void verify() {
		// ================ VERIFY ROLES ================
		// refresh the roles from the config file and Discord
		getConfiguredRoles(true);
		getRegisteredRoles(true);

		// Check for a difference between the two lists
		ListComparator<CanvaCordRole> comparator = new ListComparator<>();
		if (!(comparator.listsIdentical(configuredRoles, registeredRoles))) {
			// Ask the user if they want to fix this problem now
			if (UserInput.askToConfirm("Some roles configured for instance " + getInstanceName() + " do not appear to be registered in the target Discord server. Attempt to create them now?", "Missing Roles")) {
				// Get a list of all roles that are configured in the file but not found on Discord
				List<CanvaCordRole> unregisteredRoles = comparator.listDifference(configuredRoles, registeredRoles);
				// Attempt to create all of those roles
				// TODO this is part of Andrew's use case
			}

		}

		// ================ VERIFY CHANNELS ================
		// TODO
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

	public List<CanvaCordRole> getConfiguredRoles(boolean refresh) {
		if (refresh || configuredRoles.isEmpty()) {
			refresh();
			configuredRoles.clear();
			JSONArray jsonRoles = getInstanceRoles();
			for (int i = 0; i < jsonRoles.length(); i++) {
				configuredRoles.add(new CanvaCordRole(jsonRoles.getJSONObject(i)));
			}
		}
		return configuredRoles;
	}

	public List<CanvaCordRole> getRegisteredRoles(boolean refresh) {
		if (refresh || registeredRoles.isEmpty()) {
			// TODO fetch from Discord
		}
		return registeredRoles;
	}

	public List<CanvaCordNotification> getConfiguredNotifications(boolean refresh) {
		if (refresh || configuredNotifications.isEmpty()) {
			refresh();
			configuredNotifications.clear();
			JSONArray jsonNotifications = getInstanceNotifications();
			for (int i = 0; i < jsonNotifications.length(); i++) {
				configuredNotifications.add(new CanvaCordNotification(jsonNotifications.getJSONObject(i), configJSON.getJSONArray("roles")));
			}
		}
		return configuredNotifications;
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

	public Map<String, Boolean> getAvailableCommands(boolean refresh) {
		if (refresh || availableCommands.isEmpty()) {
			refresh();
			JSONObject availability = configJSON.getJSONObject("command_availability");
			for (String key : availability.keySet()) {
				availableCommands.put(key, availability.getBoolean(key));
			}
		}
		return availableCommands;
	}

	public Map<Long, Class<? extends Command>> getRegisteredCommands(boolean refresh) {
		// refresh from disk if requested or if the map is empty (probably never loaded)
		if (refresh || registeredCommands.isEmpty()) {
			refresh();
			// populate a map
			registeredCommands.clear();
			// load IDs from the config JSON
			JSONObject commandIDs = configJSON.optJSONObject("command_ids");
			if (commandIDs == null) commandIDs = new JSONObject();
			// iterate over each command key
			for (String key : commandIDs.keySet()) {
				long id = commandIDs.getLong(key);
				if (id != -1) {
					registeredCommands.put(id, Command.COMMANDS_BY_NAME.get(key));
				}
			}
		}
		// send it back
		return registeredCommands;
	}

	// ================================ SETTERS ================================
	public void setInstanceName(String name) {
		configJSON.put("name", name);
	}

	public void setIconPath(String path) {
		configJSON.put("icon_path", path);
	}

	public void setFetchSchedule(JSONObject fetchSchedule) {
		configJSON.put("canvas_fetch_schedule", fetchSchedule);
	}

	public void setConfiguredRoles(JSONArray rolesArray) {
		configJSON.put("roles", rolesArray);
	}

	public void setConfiguredNotifications(JSONArray notificationsArray) {
		configJSON.put("notifications", notificationsArray);
	}

	public void setHasSyllabus(boolean hasSyllabus) {
		configJSON.put("has_syllabus", hasSyllabus);
	}

	public void setTextbooks(JSONArray textbooksArray) {
		configJSON.put("textbook_files", textbooksArray);
	}

	public void setDoMeetingReminders(boolean doMeetingReminders) {
		configJSON.put("do_meeting_reminders", doMeetingReminders);
	}

	public void setDoMeetingMarkers(boolean doMeetingMarkers) {
		configJSON.put("do_meeting_markers", doMeetingMarkers);
	}

	public void setCreateRemindersRole(boolean createRemindersRole) {
		configJSON.put("create_reminders_role", createRemindersRole);
	}

	public void setClassRemindersSchedule(int remindersSchedule) {
		configJSON.put("reminders_schedule", remindersSchedule);
	}

	public void setCreateMarkersRole(boolean createMarkersRole) {
		configJSON.put("create_markers_role", createMarkersRole);
	}

	public void setGenerateExamEvents(boolean generateExamEvents) {
		configJSON.put("generate_exam_events", generateExamEvents);
	}

	public void setDoCustomReminders(boolean doCustomReminders) {
		configJSON.put("do_custom_reminders", doCustomReminders);
	}

	public void setCommandAvailability(JSONObject commandAvailability) {
		configJSON.put("command_availability", commandAvailability);
	}

	// ================================ UTILITY ================================
	public boolean writeChanges() {
		return FileUtil.writeJSON(configJSON, CanvaCordPaths.getInstanceConfigPath(getCourseID(), getServerID()).toFile());
	}

	public static InstanceConfiguration defaultConfiguration(String courseID, long serverID) {
		JSONObject defaultConfig = new JSONObject(defaultConfigJSON);
		defaultConfig.put("course_id", courseID);
		defaultConfig.put("server_id", serverID);
		return new InstanceConfiguration(defaultConfig);
	}

}
