package org.canvacord.scheduler;

import org.canvacord.instance.Instance;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class ReminderScheduler {

	private static Scheduler fetchScheduler;
	private static final String GROUP_ID = "remindMe";

	/**
	 * Initialize the Reminder scheduler. This prepares it for
	 * scheduling reminder jobs for Instances.
	 * @throws SchedulerException if something goes wrong getting the default scheduler
	 */
	public static void init() throws SchedulerException {

		fetchScheduler = StdSchedulerFactory.getDefaultScheduler();

		System.out.println("Reminder Scheduler initialized");

	}

	public static void scheduleInstance(Instance instance) throws SchedulerException {
		// TODO
	}

	public static void removeInstance(Instance instance) throws SchedulerException {
		// TODO
	}

}
