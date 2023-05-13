package org.canvacord.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.instance.Instance;
import org.canvacord.reminder.Reminder;
import org.canvacord.reminder.ReminderManager;
import org.canvacord.scheduler.job.ReminderJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class ReminderScheduler {

	private static final Logger LOGGER = LogManager.getLogger(ReminderScheduler.class);

	private static Scheduler reminderScheduler;
	private static final String GROUP_ID = "remindMe";

	/**
	 * Initialize the Reminder scheduler. This prepares it for
	 * scheduling reminder jobs for Instances.
	 * @throws SchedulerException if something goes wrong getting the default scheduler
	 */
	public static void init() throws SchedulerException {

		reminderScheduler = StdSchedulerFactory.getDefaultScheduler();

		LOGGER.debug("Reminder Scheduler initialized");

	}

	public static void scheduleInstance(Instance instance) throws SchedulerException {

		for (Reminder reminder : ReminderManager.getRemindersForInstance(instance))
			scheduleReminder(instance, reminder);

		LOGGER.debug("Scheduled reminders for instance " + instance.getName());

	}

	public static void removeInstance(Instance instance) throws SchedulerException {

		for (Reminder reminder : ReminderManager.getRemindersForInstance(instance)) {
			JobKey key = new JobKey(instance.getInstanceID() + "_" + reminder.reminderID(), GROUP_ID);
			reminderScheduler.deleteJob(key);
		}

		LOGGER.debug("Removed reminders for instance " + instance.getInstanceID());

	}

	// ================================ UTILITY ================================
	public static void scheduleReminder(Instance instance, Reminder reminder) throws SchedulerException {

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("reminder", reminder);
		dataMap.put("instance", instance);

		// Build a job for sending reminders
		JobDetail reminderJob = JobBuilder.newJob(ReminderJob.class)
				.withIdentity(instance.getInstanceID() + "_" + reminder.reminderID(), GROUP_ID)
				.usingJobData(dataMap)
				.build();

		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(instance.getInstanceID() + "_" + reminder.reminderID(), GROUP_ID)
				.startAt(reminder.getTriggerDateAsDate())
				.withSchedule(
					SimpleScheduleBuilder.simpleSchedule()
							.withRepeatCount(0)
							.withIntervalInMinutes(1)
				).build();

		reminderScheduler.scheduleJob(reminderJob, trigger);

		LOGGER.debug("Scheduled a reminder");

	}

}
