package org.canvacord.instance;

import org.canvacord.discord.commands.Command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Instance {

	// enforcing uniqueness
	private static final Set<String> courseIDs = new HashSet<>();
	private static final Set<Long> serverIDs = new HashSet<>();
	private static final HashMap<Long,Command> serverCommands = new HashMap<>();

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
		// set up a default configuration
		this.configuration = InstanceConfiguration.defaultConfiguration();
	}

	public Instance(String courseID, long serverID, InstanceConfiguration configuration) throws InstantiationException {
		this(courseID, serverID);
		this.configuration = configuration;
	}

	// ******************************** OPERATIONS ********************************
	public boolean initialize() {
		// TODO
		return false;
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

	public HashMap<Long,Command> getRegisteredCommands(){ return serverCommands;} //TODO: actually populate this hashmap?

	public String getStatus() {
		// TODO
		return "Idle";
	}

}
