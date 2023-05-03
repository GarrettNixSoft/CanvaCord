package org.canvacord.scheduler;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.CanvasFetchJob;
import org.canvacord.util.time.CanvaCordTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class CanvasFetchScheduler {

	private static Scheduler fetchScheduler;
	private static final String GROUP_ID = "canvasFetch";

	/**
	 * Initialize the Fetch scheduler. This prepares it for
	 * scheduling fetch jobs for Instances.
	 * @throws SchedulerException if something goes wrong getting the default scheduler
	 */
	public static void init() throws SchedulerException {

		fetchScheduler = StdSchedulerFactory.getDefaultScheduler();

		System.out.println("Canvas Fetch Scheduler initialized");

	}

	/**
	 * Schedule all fetch jobs for the given Instance.
	 * @param instance if something goes wrong registering the jobs
	 */
	public static void scheduleInstance(Instance instance) throws SchedulerException {

		// Build a job for fetching from Canvas
		JobDetail fetchJob = JobBuilder.newJob(CanvasFetchJob.class)
				.withIdentity(instance.getInstanceID(), GROUP_ID)
				.usingJobData("instanceID", instance.getInstanceID())
				.build();

		// Build a trigger for the job
		// TODO make this dynamic based on instance config

		// Set up the trigger builder
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		triggerBuilder.withIdentity(instance.getInstanceID(), GROUP_ID);
//		triggerBuilder.startNow();
		triggerBuilder.withSchedule(buildFetchSchedule(instance.getCanvasFetchSchedule(), triggerBuilder));

		// Execute the build
		Trigger trigger = triggerBuilder.build();

		// Send it to the scheduler
		fetchScheduler.scheduleJob(fetchJob, trigger);

		// Log it
		System.out.println("Scheduled updates for instance " + instance.getInstanceID());

	}

	/**
	 * Remove an Instance's fetch jobs from the scheduler.
	 * @param instance the Instance to remove
	 * @throws SchedulerException if something goes wrong removing the Instance's jobs
	 */
	public static void removeInstance(Instance instance) throws SchedulerException {
		JobKey key = new JobKey(instance.getInstanceID(), GROUP_ID);
		fetchScheduler.deleteJob(key);
	}

	// ================================ UTILITY ================================
	public static ScheduleBuilder buildFetchSchedule(JSONObject fetchScheduleData, TriggerBuilder<Trigger> triggerBuilder) {
		ScheduleBuilder result;
		// switch on the type of schedule we're working with
		switch (fetchScheduleData.getString("type")) {
			case "interval" -> {
				result = SimpleScheduleBuilder.simpleSchedule();
				// interval types contain an interval object describing what unit and the amount
				JSONObject intervalData = fetchScheduleData.getJSONObject("interval");
				int amount = intervalData.getInt("amount");
				// the InstanceCreateWizard sets frequent and hourly fetch types as minute and hour interval types
				switch (intervalData.getString("unit")) {
					case "minutes" -> ((SimpleScheduleBuilder) result).withIntervalInMinutes(amount);
					case "hours" -> ((SimpleScheduleBuilder) result).withIntervalInHours(amount);
					default -> throw new CanvaCordException("Invalid interval unit: " + intervalData.getString("unit"));
				}
				((SimpleScheduleBuilder) result).repeatForever();
			}
			case "daily" -> {
				// daily types describe the schedule directly in the fields
				int hour = fetchScheduleData.getInt("hour");
				int minute = fetchScheduleData.getInt("minute");
				String ampm = fetchScheduleData.getString("ampm");
				// set the trigger start time
				Date startTime = CanvaCordTime.getDateOf(hour, minute, ampm);
				triggerBuilder.startAt(startTime);
				// schedule it to repeat at 24 hour intervals
				result = SimpleScheduleBuilder.simpleSchedule();
				((SimpleScheduleBuilder) result).withIntervalInHours(24);
			}
			case "weekly" -> {
				// weekly types specify what days and at what time
				int hour = fetchScheduleData.getInt("hour");
				int minute = fetchScheduleData.getInt("minute");
				String ampm = fetchScheduleData.getString("ampm");
				JSONArray days = fetchScheduleData.getJSONArray("days");
				// modify the hour based on AM or PM
				hour = CanvaCordTime.get24Hour(hour, ampm);
				// store days as integers
				Integer[] daysOfWeek = CanvaCordTime.stringsToDayConstants(days);
				// build a cron schedule out of it
				result = CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(hour, minute, daysOfWeek);
			}
			case "cron" -> {
				// custom types specify a cron string
				String cron = fetchScheduleData.getString("cron");
				result = CronScheduleBuilder.cronSchedule(cron);
			}
			default -> throw new CanvaCordException("Invalid fetch schedule type: " + fetchScheduleData.getString("type"));
		}
		// TODO determine end date
		return result;
	}

}
