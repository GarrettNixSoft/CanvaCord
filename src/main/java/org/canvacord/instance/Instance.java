package org.canvacord.instance;

import java.util.HashSet;
import java.util.Set;

public class Instance {

	// enforcing uniqueness
	private static final Set<String> courseIDs = new HashSet<>();
	private static final Set<Long> serverIDs = new HashSet<>();

	// instance identity
	private final String courseID;
	private final long serverID;

	// components
	// TODO

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
	}

	// ******************************** GETTERS ********************************
	public String getCourseID() {
		return courseID;
	}

	public long getServerID() {
		return serverID;
	}

	// ******************************** OPERATIONS ********************************
	public String getStatus() {
		// TODO
		return "Idle";
	}

}
