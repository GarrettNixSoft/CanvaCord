package org.canvacord.persist;

import edu.ksu.canvas.model.announcement.Announcement;
import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.exception.CanvaCordException;
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

	public static InstanceData createInstanceData(String instanceID) throws CanvaCordException {

		File targetFile = Paths.get("instances/" + instanceID + "/data.json").toFile();

		if (targetFile.exists())
			throw new CanvaCordException("Data for instance " + instanceID + " already exists!");

		else {

			JSONObject emptyInstanceData = new JSONObject();
			emptyInstanceData.put("new_assignments", new JSONArray());
			emptyInstanceData.put("past_assignments", new JSONArray());
			emptyInstanceData.put("new_announcements", new JSONArray());
			emptyInstanceData.put("past_announcements", new JSONArray());

			if (!FileUtil.writeJSON(emptyInstanceData, targetFile))
				throw new CanvaCordException("Failed to create data file for instance " + instanceID);

			InstanceData result = new InstanceData(emptyInstanceData);
			cacheData.put(instanceID, result);

			return result;

		}

	}

	public static InstanceData loadInstanceData(String instanceID) throws CanvaCordException {

		// Find the data file
		File instanceDataFile = Paths.get("instances/" + instanceID + "/data.json").toFile();

		// If it exists, read the JSON and pack it
		Optional<JSONObject> loadedData = FileUtil.getJSON(instanceDataFile);
		if (loadedData.isPresent()) {
			InstanceData result = new InstanceData(loadedData.get());
			cacheData.put(instanceID, result);
			return result;
		}
		// Otherwise
		else {
			UserInput.showWarningMessage("Data cache file for " + instanceID + " not found.\nA new file will be created.", "Missing Cache Data");
			return createInstanceData(instanceID);
		}

	}

	public static void writeInstanceData(String instanceID) {

		File targetFile = Paths.get("instances/" + instanceID + "/data.json").toFile();
		if (!FileUtil.writeJSON(cacheData.get(instanceID).getJSON(), targetFile))
			throw new CanvaCordException("Failed to write cache data for instance " + instanceID);

	}

	public static void updateAssignments(String instanceID, List<Assignment> assignments) {

		// Fetch the cached data for the instance
		InstanceData targetData = cacheData.get(instanceID);
		if (targetData == null) throw new CanvaCordException("Cache data for " + instanceID + " not found!");

		// Process all assignments in the cache
		for (Assignment assignment : assignments)
			targetData.processAssignment(assignment.getId());

	}

	public static void updateAnnouncements(String instanceID, List<Announcement> announcements) {

		// Fetch the cached data for the instance
		InstanceData targetData = cacheData.get(instanceID);
		if (targetData == null) throw new CanvaCordException("Cache data for " + instanceID + " not found!");

		// Process all announcements in the cache
		for (Announcement announcement : announcements)
			targetData.processAnnouncement(announcement.getId());

	}

}
