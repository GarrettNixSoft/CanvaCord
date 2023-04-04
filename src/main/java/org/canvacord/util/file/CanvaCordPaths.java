package org.canvacord.util.file;

import org.canvacord.instance.Instance;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CanvaCordPaths {

	public static Path getInstanceDirPath(String courseID, long serverID) {
		return Paths.get("instances/" + instanceFormat(courseID, serverID));
	}

	public static Path getInstanceDirPath(Instance instance) {
		return getInstanceDirPath(instance.getCourseID(), instance.getServerID());
	}

	public static Path getInstanceDirPath(String instanceID) {
		String[] tokens = instanceID.split("-");
		return getInstanceDirPath(tokens[0], Long.parseLong(tokens[1]));
	}

	public static Path getInstanceConfigPath(String courseID, long serverID) {
		return Paths.get("instances/" + instanceFormat(courseID, serverID) + "/config.json");
	}

	public static Path getInstanceConfigPath(Instance instance) {
		return getInstanceConfigPath(instance.getCourseID(), instance.getServerID());
	}

	public static Path getInstanceConfigPath(String instanceID) {
		String[] tokens = instanceID.split("-");
		return getInstanceConfigPath(tokens[0], Long.parseLong(tokens[1]));
	}

	public static Path getInstanceCachePath(String courseID, long serverID) {
		return Paths.get("instances/" + instanceFormat(courseID, serverID) + "/data.json");
	}

	public static Path getInstanceCachePath(Instance instance) {
		return getInstanceCachePath(instance.getCourseID(), instance.getServerID());
	}

	public static Path getInstanceCachePath(String instanceID) {
		String[] tokens = instanceID.split("-");
		return getInstanceCachePath(tokens[0], Long.parseLong(tokens[1]));
	}

	public static Path getInstanceRemindersPath(String courseID, long serverID) {
		return Paths.get("instances/" + instanceFormat(courseID, serverID) + "/reminders.json");
	}

	public static Path getInstanceRemindersPath(Instance instance) {
		return getInstanceRemindersPath(instance.getCourseID(), instance.getServerID());
	}

	public static Path getInstanceRemindersPath(String instanceID) {
		String[] tokens = instanceID.split("-");
		return getInstanceRemindersPath(tokens[0], Long.parseLong(tokens[1]));
	}

	public static String instanceFormat(String courseID, long serverID) {
		return courseID + "-" + serverID;
	}

	public static String[] parseID(String instanceID) {
		return instanceID.split("-");
	}

}
