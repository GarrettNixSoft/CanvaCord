package org.canvacord.persist;

import edu.ksu.canvas.model.Module;
import edu.ksu.canvas.model.announcement.Announcement;
import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.canvas.CanvasApi;
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
	private static final Map<String, Map<Long, Module>> moduleCache = new HashMap<>();
	private static final Map<String, JSONArray> moduleEntityCache = new HashMap<>();

	private static final Map<String, Map<Long, Pair<Date, Date>>> cachedChangedDueDates = new HashMap<>();

	public static InstanceData createInstanceData(Instance instance) throws CanvaCordException {

		File targetFile = Paths.get("instances/" + instance.getInstanceID() + "/data.json").toFile();

		if (targetFile.exists())
			throw new CanvaCordException("Data for instance " + instance.getName() + " already exists!");

		else {

			JSONObject emptyInstanceData = new JSONObject();

			for (CanvaCordNotification notification : instance.getConfiguredNotifications(true)) {
				emptyInstanceData.put(notification.getName() + "_new", new JSONArray());
				emptyInstanceData.put(notification.getName() + "_old", new JSONArray());
			}

			emptyInstanceData.put("due_dates", new JSONObject());


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
			System.out.println("Assignment ID " + id + " is new for " + notification.getName());
			Assignment assignment = cachedAssignments.get(id);
			if (assignment != null)
				result.add(assignment);
		}

		return result;

	}

	public static List<Announcement> getNewAnnouncements(Instance instance, CanvaCordNotification notification) {

		InstanceData instanceData = CacheManager.instanceData.get(instance.getInstanceID());
		Set<Long> newAnnouncementIDs = instanceData.getNewEntities(notification);
		Map<Long, Announcement> cachedAnnouncements = CacheManager.getCachedAnnouncements(instance.getInstanceID());

		List<Announcement> result = new ArrayList<>();

		for (long id : newAnnouncementIDs) {
			Announcement announcement = cachedAnnouncements.get(id);
			if (announcement != null)
				result.add(announcement);
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

	public static void cacheModules(String instanceID, List<Module> modules) {
		for (Module module : modules) {
			moduleCache.computeIfAbsent(instanceID, k -> new HashMap<>()).put(module.getId(), module);
		}
	}

	public static void cacheModuleEntities(String courseID, JSONArray moduleEntities) {
		moduleEntityCache.put(courseID, moduleEntities);
	}

	public static Map<Long, Assignment> getCachedAssignments(String instanceID) {
		return Collections.unmodifiableMap(assignmentCache.computeIfAbsent(instanceID, k -> new HashMap<>()));
	}

	public static Map<Long, Announcement> getCachedAnnouncements(String instanceID) {
		return Collections.unmodifiableMap(announcementCache.computeIfAbsent(instanceID, k -> new HashMap<>()));
	}

	public static Map<Long, Module> getCachedModules(String instanceID, boolean refresh) {
		if (refresh) CanvasApi.getInstance().getAllModuleFiles(InstanceManager.getInstanceByID(instanceID).get().getCourseID());
		return Collections.unmodifiableMap(moduleCache.computeIfAbsent(instanceID, k -> new HashMap<>()));
	}

	public static JSONArray getCachedModuleEntities(String courseID) {
		return new JSONArray(moduleEntityCache.get(courseID));
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
		if (!dueDates.containsKey(assignment.getId())) {
			if (assignment.getDueAt() == null) {
				dueDates.put(assignment.getId(), new Date(Long.MAX_VALUE));
				System.out.println("NO DUE DATE FOR ASSIGNMENT: " + assignment.getName());
			}
			else {
				System.out.println("STORED DUE DATE: " + assignment.getDueAt());
				dueDates.put(assignment.getId(), assignment.getDueAt());
			}
			return;
		}

		// Get the saved due date and the current due date
		Date savedDueDate = dueDates.get(assignment.getId());
		Date currentDueDate = assignment.getDueAt();

		// can't do anything if there's no due date
		if (savedDueDate == null || currentDueDate == null)
			return;

		// Compare them, and if they differ, cache the change
		if (!savedDueDate.equals(currentDueDate))
			cachedChangedDueDates.computeIfAbsent(instanceID, k -> new HashMap<>())
					.put(assignment.getId(), new Pair<>(savedDueDate, currentDueDate));

		// Add the current due date to the cache
		dueDates.put(assignment.getId(), currentDueDate);
	}

	public static void markSent(Instance instance, CanvaCordNotification notification, Assignment assignment) {
		instanceData.get(instance.getInstanceID()).moveAssignment(assignment.getId(), notification);
		writeInstanceData(instance.getInstanceID());
	}

	public static void markSent(Instance instance, CanvaCordNotification notification, Announcement announcement) {
		instanceData.get(instance.getInstanceID()).moveAnnouncement(announcement.getId(), notification);
		writeInstanceData(instance.getInstanceID());
	}

}
