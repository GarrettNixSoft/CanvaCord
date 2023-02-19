package org.canvacord.instance;

import org.canvacord.discord.commands.Command;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.quartz.SchedulerException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Instance {

	// enforcing uniqueness
	private static final Set<String> courseIDs = new HashSet<>();
	private static final Set<Long> serverIDs = new HashSet<>();
	private static final Map<Long,Command> serverCommands = new HashMap<>();

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

}
