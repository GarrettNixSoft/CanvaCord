package org.canvacord.instance;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.commands.Command;
import org.canvacord.event.CanvaCordEvent;
import org.canvacord.event.FetchStage;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.persist.CacheManager;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.canvacord.util.file.FileUtil;
import org.quartz.SchedulerException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Instance {

	// enforcing uniqueness
	private static final Set<String> courseIDs = new HashSet<>();
	private static final Set<Long> serverIDs = new HashSet<>();
	private static final Map<Long,Command> serverCommands = new HashMap<>();

	public static void acknowledgeDeleted(Instance instance) {
		courseIDs.remove(instance.getCourseID());
		serverIDs.remove(instance.getServerID());
	}

	private final String instanceID;

	// instance identity
	private final String courseID;
	private final long serverID;

	// components
	private InstanceConfiguration configuration;

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
	public boolean initialize() {
		// TODO
		return false;
	}

	public void start() throws SchedulerException {
		CanvaCordScheduler.scheduleInstance(this);
	}

	public void update() {

		// Notify that a fetch has started for this instance
		CanvaCordEvent.newEvent(CanvaCordEvent.Type.FETCH_STARTED, this);

		// Grab the CanvasApi instance
		CanvasApi canvasApi = CanvasApi.getInstance();

		try {
			// Fetch Canvas data and update the cache

			// First fetch assignments
			CanvaCordEvent.newEvent(CanvaCordEvent.Type.FETCH_UPDATE, this, FetchStage.ASSIGNMENTS);
			CacheManager.updateAssignments(instanceID, canvasApi.getAssignments(courseID));

			// Next fetch announcements
			CanvaCordEvent.newEvent(CanvaCordEvent.Type.FETCH_UPDATE, this, FetchStage.ANNOUNCEMENTS);
			CacheManager.updateAnnouncements(instanceID, canvasApi.getAnnouncements(courseID));

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

	public String getSyllabusPath(){
		return configuration.getSyllabusPath();
	}

	public InstanceConfiguration getConfiguration() {
		return configuration;
	}

	public Map<Long,Command> getRegisteredCommands() {
		//TODO: actually populate this hashmap?
		return serverCommands;
	}

	public String getStatus() {
		// TODO
		return "Idle";
	}

	// ================ UTILITIES ================
	public static boolean isValidInstanceData(File[] dirContents) {
		return 	(FileUtil.getFileName(dirContents[0]).equals("config") &&
				FileUtil.getFileName(dirContents[1]).equals("data")) ||
				(FileUtil.getFileName(dirContents[0]).equals("data") &&
				FileUtil.getFileName(dirContents[1]).equals("config"));
	}

}
