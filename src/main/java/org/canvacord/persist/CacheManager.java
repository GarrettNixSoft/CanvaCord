package org.canvacord.persist;

import edu.ksu.canvas.model.announcement.Announcement;
import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.data.Pair;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class CacheManager {

	private static final HashMap<String, InstanceData> instanceData = new HashMap<>();

	private static final Map<String, Map<Long, Assignment>> assignmentCache = new HashMap<>();
	private static final Map<String, Map<Long, Announcement>> announcementCache = new HashMap<>();

	private static final Map<String, Map<Long, Pair<Date, Date>>> cachedChangedDueDates = new HashMap<>();

	public static void init() {
		for (Instance instance : InstanceManager.getInstances()) {
			// TODO
		}
	}

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
			instanceData.put(instance.getInstanceID(), result);

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
			instanceData.put(instance.getInstanceID(), result);
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
		if (!FileUtil.writeJSON(instanceData.get(instanceID).getJSON(), targetFile))
			throw new CanvaCordException("Failed to write cache data for instance " + instanceID);

	}

	public static void updateAssignments(Instance instance, List<Assignment> assignments) {

		// Fetch the cached data for the instance
		InstanceData targetData = instanceData.get(instance.getInstanceID());
		if (targetData == null) throw new CanvaCordException("Cache data for " + instance.getName() + " not found!");

		// Process all assignments in the cache
		for (Assignment assignment : assignments)
			targetData.processAssignment(assignment.getId());

	}

	public static void updateAnnouncements(Instance instance, List<Announcement> announcements) {

		// Fetch the cached data for the instance
		InstanceData targetData = instanceData.get(instance.getInstanceID());
		if (targetData == null) throw new CanvaCordException("Cache data for " + instance.getName() + " not found!");

		// Process all announcements in the cache
		for (Announcement announcement : announcements)
			targetData.processAnnouncement(announcement.getId());

	}

	public static List<Assignment> getNewAssignments(Instance instance, CanvaCordNotification notification) {

		InstanceData instanceData = CacheManager.instanceData.get(instance.getInstanceID());
		Set<Long> newAssignmentIDs = instanceData.getNewEntities(notification);
		Map<Long, Assignment> cachedAssignments = CacheManager.getCachedAssignments(instance.getInstanceID());

		List<Assignment> result = new ArrayList<>();

		for (long id : newAssignmentIDs) {
			result.add(cachedAssignments.get(id));
		}

		return result;

	}

	public static List<Announcement> getNewAnnouncements(Instance instance, CanvaCordNotification notification) {

		InstanceData instanceData = CacheManager.instanceData.get(instance.getInstanceID());
		Set<Long> newAnnouncementIDs = instanceData.getNewEntities(notification);
		Map<Long, Announcement> cachedAnnouncements = CacheManager.getCachedAnnouncements(instance.getInstanceID());

		List<Announcement> result = new ArrayList<>();

		for (long id : newAnnouncementIDs) {
			result.add(cachedAnnouncements.get(id));
		}

		return result;

	}

	public static void cacheAssignments(String instanceID, List<Assignment> assignments) {
		for (Assignment assignment : assignments) {
			checkDueDateChanged(instanceID, assignment);
			assignmentCache.computeIfAbsent(instanceID, k -> new HashMap<>()).put(assignment.getId(), assignment);
		}
	}

	public static void cacheAnnouncements(String instanceID, List<Announcement> announcements) {
		for (Announcement announcement : announcements) {
			announcementCache.computeIfAbsent(instanceID, k -> new HashMap<>()).put(announcement.getId(), announcement);
		}
	}

	public static Map<Long, Assignment> getCachedAssignments(String instanceID) {
		return Collections.unmodifiableMap(assignmentCache.get(instanceID));
	}

	public static Map<Long, Announcement> getCachedAnnouncements(String instanceID) {
		return Collections.unmodifiableMap(announcementCache.get(instanceID));
	}

	public static Map<Long, Date> getCachedDueDates(String instanceID) {
		return Collections.unmodifiableMap(instanceData.get(instanceID).getCachedDueDates());
	}

	public static Map<Long, Pair<Date, Date>> getCachedChangedDueDates(String instanceID) {
		return cachedChangedDueDates.get(instanceID);
	}

	private static void checkDueDateChanged(String instanceID, Assignment assignment) {

		Map<Long, Date> dueDates = instanceData.get(instanceID).getCachedDueDates();

		// can't detect a changed due date if there's no previously saved due date
		if (!dueDates.containsKey(assignment.getId())) return;

		// Get the saved due date and the current due date
		Date savedDueDate = dueDates.get(assignment.getId());
		Date currentDueDate = assignment.getDueAt();

		// Compare them, and if they differ, cache the change
		if (!savedDueDate.equals(currentDueDate))
			cachedChangedDueDates.computeIfAbsent(instanceID, k -> new HashMap<>())
					.put(assignment.getId(), new Pair<>(savedDueDate, currentDueDate));

		// Add the current due date to the cache
		dueDates.put(assignment.getId(), currentDueDate);
	}

}
