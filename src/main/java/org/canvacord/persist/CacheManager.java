package org.canvacord.persist;

import edu.ksu.canvas.model.announcement.Announcement;
import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CacheManager {

	private static final HashMap<String, InstanceData> cacheData = new HashMap<>();

	public static InstanceData createInstanceData(Instance instance) throws CanvaCordException {

		File targetFile = Paths.get("instances/" + instance.getInstanceID() + "/data.json").toFile();

		if (targetFile.exists())
			throw new CanvaCordException("Data for instance " + instance.getName() + " already exists!");

		else {

			JSONObject emptyInstanceData = new JSONObject();

			System.out.println("instance has " + instance.getConfiguredNotifications(false).size() + " notifications");

			for (CanvaCordNotification notification : instance.getConfiguredNotifications(true)) {
				emptyInstanceData.put(notification.getName() + "_new_assignments", new JSONArray());
				emptyInstanceData.put(notification.getName() + "_past_assignments", new JSONArray());
				emptyInstanceData.put(notification.getName() + "_new_announcements", new JSONArray());
				emptyInstanceData.put(notification.getName() + "_past_announcements", new JSONArray());
			}


			if (!FileUtil.writeJSON(emptyInstanceData, targetFile))
				throw new CanvaCordException("Failed to create data file for instance " + instance.getName());

			InstanceData result = new InstanceData(instance, emptyInstanceData);
			cacheData.put(instance.getInstanceID(), result);

			return result;

		}

	}

	public static InstanceData loadInstanceData(Instance instance) throws CanvaCordException {

		// Find the data file
		File instanceDataFile = CanvaCordPaths.getInstanceCachePath(instance).toFile();

		// If it exists, read the JSON and pack it
		Optional<JSONObject> loadedData = FileUtil.getJSON(instanceDataFile);
		if (loadedData.isPresent()) {
			InstanceData result = new InstanceData(instance, loadedData.get());
			cacheData.put(instance.getInstanceID(), result);
			return result;
		}
		// Otherwise
		else {
			UserInput.showWarningMessage("Data cache file for " + instance.getName() + " not found.\nA new file will be created.", "Missing Cache Data");
			return createInstanceData(instance);
		}

	}

	public static void writeInstanceData(String instanceID) {

		File targetFile = Paths.get("instances/" + instanceID + "/data.json").toFile();
		if (!FileUtil.writeJSON(cacheData.get(instanceID).getJSON(), targetFile))
			throw new CanvaCordException("Failed to write cache data for instance " + instanceID);

	}

	public static void updateAssignments(Instance instance, List<Assignment> assignments) {

		// Fetch the cached data for the instance
		InstanceData targetData = cacheData.get(instance.getInstanceID());
		if (targetData == null) throw new CanvaCordException("Cache data for " + instance.getName() + " not found!");

		// Process all assignments in the cache
		for (Assignment assignment : assignments)
			targetData.processAssignment(assignment.getId());

	}

	public static void updateAnnouncements(Instance instance, List<Announcement> announcements) {

		// Fetch the cached data for the instance
		InstanceData targetData = cacheData.get(instance.getInstanceID());
		if (targetData == null) throw new CanvaCordException("Cache data for " + instance.getName() + " not found!");

		// Process all announcements in the cache
		for (Announcement announcement : announcements)
			targetData.processAnnouncement(announcement.getId());

	}

}
