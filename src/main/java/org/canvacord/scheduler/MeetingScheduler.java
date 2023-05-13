package org.canvacord.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.CanvasFetchJob;
import org.canvacord.scheduler.job.MeetingMarkerJob;
import org.canvacord.scheduler.job.MeetingReminderJob;
import org.canvacord.util.data.Pair;
import org.canvacord.util.time.CanvaCordTime;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class MeetingScheduler {

	private static final Logger LOGGER = LogManager.getLogger();

	private static Scheduler meetingScheduler;
	private static final String GROUP_ID = "meetings";

	private static Map<Instance, List<JobKey>> meetingJobs;

	/**
	 * Initialize the Meeting scheduler. This prepares it for
	 * scheduling meeting jobs for Instances.
	 * @throws SchedulerException if something goes wrong getting the default scheduler
	 */
	public static void init() throws SchedulerException {

		meetingScheduler = StdSchedulerFactory.getDefaultScheduler();
		meetingJobs = new HashMap<>();
		LOGGER.debug("Meeting Scheduler initialized");

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

			// Add a list to the key map
			meetingJobs.put(instance, new ArrayList<>());

			// Log it
			LOGGER.debug("Scheduled meeting reminders for instance " + instance.getInstanceID());
		}

		if (instance.doMeetingMarkers()) {

			// build triggers for each class meeting
			List<Pair<Trigger, Trigger>> triggers = buildMarkerSchedule(instance);
			List<ClassMeeting> classMeetings = instance.getClassSchedule();

			// validate the trigger count
			if (triggers.size() != classMeetings.size())
				throw new CanvaCordException("Trigger count mismatch! Found "
						+ classMeetings.size() + " class meetings, but got "
						+ triggers.size() + " triggers");

			for (int i = 0; i < triggers.size(); i++) {
				// Get the associated meeting
				ClassMeeting classMeeting = classMeetings.get(i);
				// prepare start job builder
				JobBuilder markerStartJob = JobBuilder.newJob(MeetingMarkerJob.class)
						.withIdentity(instance.getInstanceID() + classMeeting.getTimeDescription() + "_marker_start", GROUP_ID)
						.usingJobData("instanceID", instance.getInstanceID());
				// prepare end job builder
				JobBuilder markerEndJob = JobBuilder.newJob(MeetingMarkerJob.class)
						.withIdentity(instance.getInstanceID() + classMeeting.getTimeDescription() + "_marker_end", GROUP_ID)
						.usingJobData("instanceID", instance.getInstanceID());
				// Build the start job
				JobDataMap startDataMap = new JobDataMap();
				startDataMap.put("instance", instance);
				startDataMap.put("meeting", classMeetings.get(i));
				startDataMap.put("type", "start");
				markerStartJob.usingJobData(startDataMap);
				// Build the end job
				JobDataMap endDataMap = new JobDataMap();
				endDataMap.put("instance", instance);
				endDataMap.put("meeting", classMeetings.get(i));
				endDataMap.put("type", "end");
				markerEndJob.usingJobData(endDataMap);
				// SCHEDULE THE JOBS
				meetingScheduler.scheduleJob(markerStartJob.build(), triggers.get(i).first());
				meetingScheduler.scheduleJob(markerEndJob.build(), triggers.get(i).second());
				// SAVE THE KEYS
				meetingJobs.get(instance).add(new JobKey(instance.getInstanceID() + classMeeting.getTimeDescription() + "_marker_start", GROUP_ID));
				meetingJobs.get(instance).add(new JobKey(instance.getInstanceID() + classMeeting.getTimeDescription() + "_marker_end", GROUP_ID));
			}
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
			for (JobKey jobKey : meetingJobs.get(instance)) {
				meetingScheduler.deleteJob(jobKey);
			}
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

	private static List<Pair<Trigger, Trigger>> buildMarkerSchedule(Instance instance) {
		List<Pair<Trigger, Trigger>> triggers = new ArrayList<>();
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
			startTriggerBuilder.withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(weekday, startHour, startMinute));
			endTriggerBuilder.withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(weekday, endHour, endMinute));

			// build the triggers and add them to the list
			triggers.add(new Pair<>(startTriggerBuilder.build(), endTriggerBuilder.build()));

		}
		return triggers;
	}

}
