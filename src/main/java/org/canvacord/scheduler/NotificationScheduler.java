package org.canvacord.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.event.CanvaCordEvent;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.NotificationJob;
import org.canvacord.util.time.CanvaCordTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.List;

public class NotificationScheduler {

	private static final Logger LOGGER = LogManager.getLogger();

	private static Scheduler notifyScheduler;
	private static final String GROUP_ID = "discordNotify";

	/**
	 * Initialize the Notification scheduler. This prepares it for
	 * scheduling notification jobs for Instances.
	 * @throws SchedulerException if something goes wrong getting the default scheduler
	 */
	public static void init() throws SchedulerException {

		notifyScheduler = StdSchedulerFactory.getDefaultScheduler();

		LOGGER.debug("Notification Scheduler initialized");

	}

	/**
	 * Schedule all notification jobs for the given Instance.
	 * @param instance if something goes wrong registering the jobs
	 */
	public static void scheduleInstance(Instance instance) throws SchedulerException {

		List<CanvaCordNotification> notifications =  instance.getConfiguredNotifications(false);

		for (CanvaCordNotification notification : notifications)
			scheduleNotification(instance, notification);

		LOGGER.debug("Scheduled notifications for instance " + instance.getInstanceID());

	}

	/**
	 * Remove an Instance's notification jobs from the scheduler.
	 * @param instance the Instance to remove
	 * @throws SchedulerException if something goes wrong removing the Instance's jobs
	 */
	public static void removeInstance(Instance instance) throws SchedulerException {

		List<CanvaCordNotification> notifications =  instance.getConfiguredNotifications(false);

		for (CanvaCordNotification notification : notifications) {
			JobKey key = new JobKey(instance.getInstanceID() + "_" + notification.getName(), GROUP_ID);
			notifyScheduler.deleteJob(key);
		}

		LOGGER.debug("Removed notifications for instance " + instance.getInstanceID());

	}

	// ================================ UTILITY ================================
	private static void scheduleNotification(Instance instance, CanvaCordNotification notification) throws SchedulerException {

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("notification", notification);

		// TODO
		// Build a job for sending notifications
		JobDetail notifyJob = JobBuilder.newJob(NotificationJob.class)
				.withIdentity(instance.getInstanceID() + "_" + notification.getName(), GROUP_ID)
				.usingJobData("server", instance.getServerID())
				.usingJobData(dataMap)
				.build();

		// Build a trigger for the job
		TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger();
		triggerBuilder.withIdentity(instance.getInstanceID() + "_" + notification.getName() + "-trigger", GROUP_ID);

		// Configure a schedule
		ScheduleBuilder scheduleBuilder;

		if (notification.getEventType() == CanvaCordEvent.Type.ASSIGNMENT_DUE_DATE_APPROACHING) {
			// TODO this will need to be handled on a per-assignment basis
			scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
		}
		else {
			JSONObject scheduleData = notification.getSchedule();
			switch (scheduleData.getString("type")) {
				case "interval" -> {
					// intervals begin immediately
					triggerBuilder.startNow();
					// interval types store an interval object with a unit and a value
					JSONObject intervalData = scheduleData.getJSONObject("interval");
					int value = intervalData.getInt("value");
					String unit = intervalData.getString("unit");
					switch (unit) {
						case "minutes" -> scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(value);
						case "hours" -> scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(value);
						case "days" -> scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(value * 24);
						default -> throw new CanvaCordException("Invalid notification interval unit: " + unit);
					}
					((SimpleScheduleBuilder) scheduleBuilder).repeatForever(); // TODO determine end date
				}
				case "daily" -> {
					// daily types store a time to use each day as a time object
					JSONObject time = scheduleData.getJSONObject("time");
					int hour = time.getInt("hour");
					int minute = time.getInt("minute");
					String ampm = time.getString("ampm");
					// build a start time date
					Date startTime = CanvaCordTime.getDateOf(hour, minute, ampm);
					triggerBuilder.startAt(startTime);
					scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24);
					((SimpleScheduleBuilder) scheduleBuilder).repeatForever(); // TODO determine end date
				}
				case "weekly" -> {
					// weekly types store a time to use each day and an array of days
					int hour = scheduleData.getInt("hour");
					int minute = scheduleData.getInt("minute");
					String ampm = scheduleData.getString("ampm");
					JSONArray days = scheduleData.getJSONArray("days");
					// modify the hour to 24-hour time
					hour = CanvaCordTime.get24Hour(hour, ampm);
					// get day integers
					Integer[] daysOfWeek = CanvaCordTime.stringsToDayConstants(days);
					// build the schedule
					scheduleBuilder = CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(hour, minute, daysOfWeek);
				}
				default -> throw new CanvaCordException("Invalid notification schedule type: " + scheduleData.getString("type"));
			}
		}

		triggerBuilder.withSchedule(scheduleBuilder);

		// send the job and trigger to the scheduler
		notifyScheduler.scheduleJob(notifyJob, triggerBuilder.build());

	}

}
