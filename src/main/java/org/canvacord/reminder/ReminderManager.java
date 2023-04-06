package org.canvacord.reminder;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.main.CanvaCord;
import org.canvacord.scheduler.ReminderScheduler;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.SchedulerException;

import java.io.File;
import java.util.*;

public class ReminderManager {

	private static final Map<String, List<Reminder>> storedReminders = new HashMap<>();

	/**
	 * Initialize the ReminderManager. Loads all saved reminder data for all
	 * instances, then sends them to the scheduler.
	 * <br>
	 * Must be called after the InstanceManager has finished initializing.
	 */
	public static void init() {
		for (Instance instance : InstanceManager.getInstances()) {
			// check whether this instance has reminders enabled
			if (!instance.doCustomReminders()) continue;
			// generate and store a list for this instance
			List<Reminder> reminders = new ArrayList<>();
			storedReminders.put(instance.getInstanceID(), reminders);
			// check the reminders file for the instance
			JSONArray remindersData = checkRemindersFile(instance);
			System.out.println(remindersData.toString(4));
			// load all reminders for the instance
			for (int i = 0; i < remindersData.length(); i++) {
				JSONObject reminderData = remindersData.getJSONObject(i);
				reminders.add(Reminder.load(reminderData));
			}
		}
	}

	public static List<Reminder> getRemindersForInstance(String instanceID) {
		return Collections.unmodifiableList(storedReminders.get(instanceID));
	}

	public static List<Reminder> getRemindersForInstance(Instance instance) {
		return getRemindersForInstance(instance.getInstanceID());
	}

	public static void addNewReminder(Instance instance, Reminder reminder) {
		storedReminders.computeIfAbsent(instance.getInstanceID(), k -> new ArrayList<>()).add(reminder);
		writeRemindersFile(instance);
		try {
			ReminderScheduler.scheduleReminder(instance, reminder);
		}
		catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void registerReminderSent(Instance instance, Reminder reminder) {
		storedReminders.get(instance.getInstanceID()).remove(reminder);
		writeRemindersFile(instance);
	}

	private static JSONArray checkRemindersFile(Instance instance) {
		File remindersFile = CanvaCordPaths.getInstanceRemindersPath(instance).toFile();
		if (!remindersFile.exists()) {
			boolean created = FileUtil.writeJSON(new JSONObject(""), remindersFile);
			if (!created) {
				CanvaCord.explode("Could not create reminders file for instance " + instance.getName());
			}
		}
		return FileUtil.getJSONFileAsJSONArray(remindersFile).orElseGet(() -> new JSONArray("[]"));
	}

	private static void writeRemindersFile(Instance instance) {
		JSONArray remindersArray = new JSONArray();
		for (Reminder reminder : storedReminders.get(instance.getInstanceID())) {
			remindersArray.put(reminder.toJSON());
		}
		boolean success = FileUtil.writeJSONArray(remindersArray, CanvaCordPaths.getInstanceRemindersPath(instance).toFile());
		if (!success) throw new CanvaCordException("Failed to write reminders JSON for instance " + instance.getName());
	}

}
