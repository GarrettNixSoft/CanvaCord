package org.canvacord.instance;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.canvas.TextbookInfo;
import org.canvacord.discord.commands.Command;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.event.CanvaCordEvent;
import org.canvacord.event.FetchStage;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.persist.CacheManager;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.canvacord.util.file.FileUtil;
import org.json.JSONObject;
import org.quartz.SchedulerException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Instance {

	// enforcing uniqueness
	private static final Set<String> courseIDs = new HashSet<>();
	private static final Set<Long> serverIDs = new HashSet<>();

	public static void acknowledgeDeleted(Instance instance) {
		courseIDs.remove(instance.getCourseID());
		serverIDs.remove(instance.getServerID());
	}

	private final String instanceID;

	// instance identity
	private final String courseID;
	private final long serverID;

	// components
	private final InstanceConfiguration configuration;

	public Instance(String courseID, long serverID) throws InstantiationException {
		// enforce uniqueness
		if (courseIDs.contains(courseID))
			throw new InstantiationException("Course ID " + courseID + " is already in use by another instance!");
		else if (serverIDs.contains(serverID))
			throw new InstantiationException("Server ID " + serverID + " is already in use by another instance!");
		// if unique, store and assign fields
		courseIDs.add(courseID);
		this.courseID = courseID;
		serverIDs.add(serverID);
		this.serverID = serverID;
		// build the instance ID
		this.instanceID = courseID + "-" + serverID;
		// set up a default configuration if necessary
		this.configuration = InstanceConfiguration.defaultConfiguration(courseID, serverID);
	}

	public Instance(String courseID, long serverID, InstanceConfiguration configuration) throws InstantiationException {
		// enforce uniqueness
		if (courseIDs.contains(courseID))
			throw new InstantiationException("Course ID " + courseID + " is already in use by another instance!");
		else if (serverIDs.contains(serverID))
			throw new InstantiationException("Server ID " + serverID + " is already in use by another instance!");
		// if unique, store and assign fields
		courseIDs.add(courseID);
		this.courseID = courseID;
		serverIDs.add(serverID);
		this.serverID = serverID;
		// build the instance ID
		this.instanceID = courseID + "-" + serverID;
		// use given config
		this.configuration = configuration;
	}

	// ******************************** OPERATIONS ********************************

	/**
	 * Run the initialization process for this Instance in its target Discord server.
	 * This creates all Roles, Channels, etc. that this instance is configured to use
	 * as defined in its config.json file generated during the instance creation process.
	 * @return {@code true} if the initialization of all entities completes without error
	 * @throws CanvaCordException if some error occurs during initialization
	 */
	public boolean initialize() throws CanvaCordException {
		// TODO
		return false;
	}

	/**
	 * Verify which entities (Roles, Channels, etc.) required for this Instance's
	 * operation on the Discord end are actually present in the target Server.
	 * <br>
	 * If any entities are missing, the user will be prompted to repair the instance.
	 * If they choose to repair, CanvaCord will attempt to create the missing entities
	 * in Discord.
	 * @throws CanvaCordException if some error occurs when creating missing entities
	 */
	public void verify() throws CanvaCordException {

		configuration.verify();

	}

	/**
	 * Start this Instance. This Instance's configured Fetch and Notify schedules
	 * will be registered in the CanvaCordScheduler, putting this Instance in an active state.
	 * @throws SchedulerException if some error occurs adding this Instance to the scheduler
	 */
	public void start() throws SchedulerException {
		CanvaCordScheduler.scheduleInstance(this);
	}

	/**
	 * Perform a Canvas Fetch to update this Instance's information.
	 */
	public void update() {

		// Notify that a fetch has started for this instance
		CanvaCordEvent.newEvent(CanvaCordEvent.Type.FETCH_STARTED, this);

		// Grab the CanvasApi instance
		CanvasApi canvasApi = CanvasApi.getInstance();

		try {
			// Fetch Canvas data and update the cache

			// First fetch assignments
			CanvaCordEvent.newEvent(CanvaCordEvent.Type.FETCH_UPDATE, this, FetchStage.ASSIGNMENTS);
			CacheManager.updateAssignments(this, canvasApi.getAssignments(courseID));

			// Next fetch announcements
			CanvaCordEvent.newEvent(CanvaCordEvent.Type.FETCH_UPDATE, this, FetchStage.ANNOUNCEMENTS);
			CacheManager.updateAnnouncements(this, canvasApi.getAnnouncements(courseID));

			// Write cache data to disk
			CacheManager.writeInstanceData(instanceID);

			// Notify that the fetch has completed for this instance
			CanvaCordEvent.newEvent(CanvaCordEvent.Type.FETCH_COMPLETED, this);
		}
		catch (IOException | CanvaCordException e) {
			CanvaCordEvent.newEvent(CanvaCordEvent.Type.FETCH_ERROR, this, e);
			e.printStackTrace();
		}

	}

	public void notifyServer() {
		// TODO
	}

	public void stop() throws SchedulerException {
		CanvaCordScheduler.removeInstance(this);
	}

	// ******************************** GETTERS ********************************
	public String getInstanceID() {
		return instanceID;
	}

	public String getCourseID() {
		return courseID;
	}

	public long getServerID() {
		return serverID;
	}

	public String getName() {
		return configuration.getInstanceName();
	}

	public String getIconPath() {
		return configuration.getIconPath();
	}

	public String getCourseTitle() {
		return configuration.getCourseTitle();
	}

	public String getServerName() {
		return configuration.getServerName();
	}

	public boolean hasSyllabus(){
		return configuration.hasSyllabus();
	}

	public boolean doMeetingReminders() {
		return configuration.doMeetingReminders();
	}

	public boolean doMeetingMarkers() {
		return configuration.doMeetingMarkers();
	}

	public boolean createRemindersRole() {
		return configuration.createRemindersRole();
	}

	public boolean createMarkersRole() {
		return configuration.createMarkersRole();
	}

	public boolean generateExamEvents() {
		return configuration.generateExamEvents();
	}

	public boolean doCustomReminders() {
		return configuration.doCustomReminders();
	}

	public InstanceConfiguration getConfiguration() {
		return configuration;
	}

	public List<CanvaCordRole> getConfiguredRoles(boolean refresh) {
		return configuration.getConfiguredRoles(refresh);
	}

	public List<CanvaCordRole> getRegisteredRoles(boolean refresh) {
		return configuration.getRegisteredRoles(refresh);
	}

	public List<CanvaCordNotification> getConfiguredNotifications(boolean refresh) {
		return configuration.getConfiguredNotifications(refresh);
	}

	public JSONObject getCanvasFetchSchedule() {
		return configuration.getFetchSchedule();
	}

	public List<ClassMeeting> getClassSchedule() {
		return configuration.getClassSchedule();
	}

	public List<TextbookInfo> getTextbooks() {
		return configuration.getTextbooks();
	}

	public int getClassReminderSchedule() {
		return configuration.getClassReminderSchedule();
	}

	public Map<String, Boolean> getAvailableCommands(boolean refresh) {
		return configuration.getAvailableCommands(refresh);
	}

	public Map<Long, Class<? extends Command>> getRegisteredCommands() {
		//TODO: actually populate this hashmap?
		return configuration.getRegisteredCommands(false);
	}

	public String getStatus() {
		// TODO
		return "Idle";
	}

	// ================ UTILITIES ================
	public static boolean isValidInstanceData(File dir) {
		return 	(FileUtil.dirContainsFileNamed(dir, "config") &&
				FileUtil.dirContainsFileNamed(dir, "data"));
	}

}
