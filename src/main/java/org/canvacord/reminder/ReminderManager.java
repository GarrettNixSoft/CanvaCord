package org.canvacord.reminder;

import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.main.CanvaCord;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;

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
			// load all reminders for the instance
			for (int i = 0; i < remindersData.length(); i++) {
				JSONObject reminderData = remindersData.getJSONObject(i);
				reminders.add(Reminder.load(reminderData));
			}
		}
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

	public static List<Reminder> getRemindersForInstance(String instanceID) {
		return Collections.unmodifiableList(storedReminders.get(instanceID));
	}

	public static List<Reminder> getRemindersForInstance(Instance instance) {
		return getRemindersForInstance(instance.getInstanceID());
	}

	public static void registerReminderSent(Instance instance, Reminder reminder) {
		// TODO
	}

}
