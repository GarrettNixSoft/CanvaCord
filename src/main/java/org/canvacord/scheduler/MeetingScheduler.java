package org.canvacord.scheduler;

import org.canvacord.entity.ClassMeeting;
import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.CanvasFetchJob;
import org.canvacord.scheduler.job.MeetingMarkerJob;
import org.canvacord.scheduler.job.MeetingReminderJob;
import org.canvacord.util.time.CanvaCordTime;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeetingScheduler {

	private static Scheduler meetingScheduler;
	private static final String GROUP_ID = "meetings";

	/**
	 * Initialize the Meeting scheduler. This prepares it for
	 * scheduling meeting jobs for Instances.
	 * @throws SchedulerException if something goes wrong getting the default scheduler
	 */
	public static void init() throws SchedulerException {

		meetingScheduler = StdSchedulerFactory.getDefaultScheduler();
		System.out.println("Meeting Scheduler initialized");

	}

	/**
	 * Schedule all fetch jobs for the given Instance.
	 * @param instance if something goes wrong registering the jobs
	 */
	public static void scheduleInstance(Instance instance) throws SchedulerException {

		if (instance.doMeetingReminders()) {
			// Build a job for sending meeting reminders
			JobDetail reminderJob = JobBuilder.newJob(MeetingReminderJob.class)
					.withIdentity(instance.getInstanceID() + "_meeting_reminders", GROUP_ID)
					.usingJobData("instanceID", instance.getInstanceID())
					.build();

			// build triggers for each class meeting
			List<Trigger> triggers = buildReminderSchedule(instance);

			// Send the job to the scheduler with every trigger
			for (Trigger trigger : triggers)
				meetingScheduler.scheduleJob(reminderJob, trigger);

			// Log it
			System.out.println("Scheduled meeting reminders for instance " + instance.getInstanceID());

			// TODO
		}

		if (instance.doMeetingMarkers()) {
			// Build a job for sending meeting markers
			JobDetail markerJob = JobBuilder.newJob(MeetingMarkerJob.class)
					.withIdentity(instance.getInstanceID(), GROUP_ID)
					.usingJobData("instanceID", instance.getInstanceID())
					.build();
			// TODO
		}

	}

	/**
	 * Remove an Instance's fetch jobs from the scheduler.
	 * @param instance the Instance to remove
	 * @throws SchedulerException if something goes wrong removing the Instance's jobs
	 */
	public static void removeInstance(Instance instance) throws SchedulerException {
		if (instance.doMeetingReminders()) {
			JobKey remindersKey = new JobKey(instance.getInstanceID() + "_meeting_reminders", GROUP_ID);
			meetingScheduler.deleteJob(remindersKey);
		}
		if (instance.doMeetingMarkers()) {
			JobKey markersKey = new JobKey(instance.getInstanceID() + "_meeting_markers", GROUP_ID);
			meetingScheduler.deleteJob(markersKey);
		}
	}

	// ================================ UTILITY ================================
	private static List<Trigger> buildReminderSchedule(Instance instance) {
		List<Trigger> triggers = new ArrayList<>();
		for (ClassMeeting classMeeting : instance.getClassSchedule()) {

			// extract the meeting start time values
			int weekday = CanvaCordTime.getDateBuilderIntForWeekday(classMeeting.getWeekday());
			int startHour = classMeeting.getStartTime().getInt("hour");
			int startMinute = classMeeting.getStartTime().getInt("minute");
			int reminderOffset = instance.getClassReminderSchedule();

			// create a trigger builder with a unique identity
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
			String name = classMeeting.getWeekdayStr() + "_reminder_" + startHour + ":" + startMinute;
			triggerBuilder.withIdentity(instance.getInstanceID() + "_" + name, GROUP_ID);

			// apply the offset
			LocalDateTime offsetTime = CanvaCordTime.offsetDateByMinutes(DateBuilder.dateOf(startHour, startMinute, 0), reminderOffset);
			int hour = offsetTime.getHour();
			int minute = offsetTime.getMinute();

			// apply the schedule
			triggerBuilder.withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(weekday, hour, minute));

			// add the trigger to the list
			triggers.add(triggerBuilder.build());

		}
		return triggers;
	}

	private static List<Trigger> buildMarkerSchedule(Instance instance) {
		List<Trigger> triggers = new ArrayList<>();
		for (ClassMeeting classMeeting : instance.getClassSchedule()) {

			// extract the meeting start time values
			int weekday = CanvaCordTime.getDateBuilderIntForWeekday(classMeeting.getWeekday());
			int startHour = classMeeting.getStartTime().getInt("hour");
			int startMinute = classMeeting.getStartTime().getInt("minute");

			// extract the meeting end time values
			int endHour = classMeeting.getEndTime().getInt("hour");
			int endMinute = classMeeting.getEndTime().getInt("minute");

			// create a trigger builder with a unique identity for the start and end triggers
			TriggerBuilder<Trigger> startTriggerBuilder = TriggerBuilder.newTrigger();
			String startTriggerName = classMeeting.getWeekdayStr() + "_marker_start_" + startHour + ":" + startMinute;
			startTriggerBuilder.withIdentity(instance.getInstanceID() + "_" + startTriggerName, GROUP_ID);

			// create a trigger builder with a unique identity
			TriggerBuilder<Trigger> endTriggerBuilder = TriggerBuilder.newTrigger();
			String endTriggerName = classMeeting.getWeekdayStr() + "_marker_end_" + endHour + ":" + endMinute;
			endTriggerBuilder.withIdentity(instance.getInstanceID() + "_" + endTriggerName, GROUP_ID);

			// build the trigger schedules
			// TODO

		}
		return triggers;
	}

}
