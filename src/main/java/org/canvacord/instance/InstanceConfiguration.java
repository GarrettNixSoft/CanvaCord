package org.canvacord.instance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.canvas.TextbookInfo;
import org.canvacord.discord.DiscordBot;
import org.canvacord.discord.commands.Command;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.options.NamedError;
import org.canvacord.util.compare.ListComparator;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public class InstanceConfiguration {

	private static Logger LOGGER = LogManager.getLogger(InstanceConfiguration.class);

	private static JSONObject defaultConfigJSON;

	static {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(("default_config.json"));
		Optional<JSONObject> defaultConfigJSON = FileUtil.getJSON(inputStream);
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
	private final Map<Long, Class<? extends Command>> storedCommandMap = new HashMap<>();
	private final Map<Long, Class<? extends Command>> registeredCommandMap = new HashMap<>();

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

	public List<NamedError> verify() {

		// Prepare a list of errors
		List<NamedError> errors = new ArrayList<>();

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
				// For every role in unregisteredRoles, add a NamedError to errors
			}

		}

		// ================ VERIFY CHANNELS ================
		// TODO


		return errors;

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

	public long getMeetingRemindersChannel() {
		return configJSON.getLong("meeting_reminders_channel");
	}

	public long getMeetingRemindersRole() {
		return configJSON.getLong("reminders_role_id");
	}

	public boolean createMarkersRole() {
		return configJSON.getBoolean("create_markers_role");
	}

	public long getMeetingMarkersChannel() {
		return configJSON.getLong("meeting_markers_channel");
	}

	public long getMeetingMarkersRole() {
		return configJSON.getLong("markers_role_id");
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
			// Get API
			// Use api to get server
			// Get roles from server
			// Make sure it's logged in to get roles
			DiscordBot.getBotInstance().login();
			// For every role, make a CanvaCordRole out of it and put it in registeredRoles
			DiscordApi api = DiscordBot.getBotInstance().getApi();
			Server server = api.getServerById(getServerID()).orElseThrow();
			List<Role> roles =  server.getRoles();
			for(Role role : roles) {
				registeredRoles.add(new CanvaCordRole(role.getColor().orElse(Color.BLACK), role.getName(), role.getId()));
			}
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

	public JSONObject getCommandAvailability(boolean refresh) {
		if (refresh) refresh();
		return configJSON.getJSONObject("command_availability");
	}

	public Map<Long, Class<? extends Command>> getRegisteredCommandIDs(boolean refresh) {
		DiscordApi api = DiscordBot.getBotInstance().getApi();
		Set<SlashCommand> commands = api.getServerSlashCommands(api.getServerById(getServerID()).orElseThrow()).join();
		for(SlashCommand command : commands)
		{
			if(Command.COMMANDS_BY_NAME.containsValue(command.getName()))
				registeredCommandMap.put(command.getId(), Command.COMMANDS_BY_NAME.get(command.getName()));
		}
		return registeredCommandMap;
	}

	public Map<Long, Class<? extends Command>> getStoredCommandIDs(boolean refresh) {
		// refresh from disk if requested or if the map is empty (probably never loaded)
		if (refresh || storedCommandMap.isEmpty()) {
			refresh();
			// populate a map
			storedCommandMap.clear();
			// load IDs from the config JSON
			JSONObject commandIDs = configJSON.optJSONObject("command_ids");
			if (commandIDs == null) commandIDs = new JSONObject();
			// iterate over each command key
			for (String key : commandIDs.keySet()) {
				long id = commandIDs.getLong(key);
				if (id != -1) {
					storedCommandMap.put(id, Command.COMMANDS_BY_NAME.get(key));
				}
			}
		}
		// send it back
		return storedCommandMap;
	}

	public JSONObject getCommandIDs(boolean refresh) {
		if (refresh) refresh();
		return configJSON.getJSONObject("command_ids");
	}

	public boolean doCleanUp() {
		return configJSON.getBoolean("do_clean_up");
	}

	public String getCleanUpAction() {
		return configJSON.getString("clean_up_action");
	}

	public boolean isCleanedUp() {
		return configJSON.optBoolean("cleaned_up", false);
	}

	public boolean isInitialized() {
		return configJSON.optBoolean("initialized", false);
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

	public void setClassSchedule(List<ClassMeeting> classSchedule) {
		JSONArray meetingArray = new JSONArray();
		for (ClassMeeting classMeeting : classSchedule) {
			meetingArray.put(classMeeting.getJSON());
		}
		configJSON.put("class_schedule", meetingArray);
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

	public void setRemindersRoleID(long roleID) {
		configJSON.put("reminders_role_id", roleID);
	}

	public void setClassRemindersSchedule(int remindersSchedule) {
		configJSON.put("reminders_schedule", remindersSchedule);
	}

	public void setClassRemindersChannel(long channelID) {
		configJSON.put("meeting_reminders_channel", channelID);
	}

	public void setCreateMarkersRole(boolean createMarkersRole) {
		configJSON.put("create_markers_role", createMarkersRole);
	}

	public void setMarkersRoleID(long roleID) {
		configJSON.put("markers_role_id", roleID);
	}

	public void setClassMarkersChannel(long channelID) {
		configJSON.put("meeting_markers_channel", channelID);
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

	public void setCommandIDs(JSONObject commandIDs) {
		configJSON.put("command_ids", commandIDs);
	}

	public void setInitialized(boolean initialized) {
		configJSON.put("initialized", initialized);
	}

	public void setDoCleanUp(boolean doCleanUp) {
		configJSON.put("do_clean_up", doCleanUp);
	}

	public void setCleanUpAction(String cleanUpAction) {
		configJSON.put("clean_up_action", cleanUpAction);
	}

	public void markCleanedUp() {
		configJSON.put("cleaned_up", true);
	}

	public void saveRoles() {

		JSONArray rolesArray = new JSONArray();
		for (CanvaCordRole role : configuredRoles) {
			rolesArray.put(role.getJSON());
		}
		configJSON.put("roles", rolesArray);

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
