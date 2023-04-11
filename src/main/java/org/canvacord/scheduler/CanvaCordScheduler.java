package org.canvacord.scheduler;

import org.canvacord.instance.Instance;
import org.canvacord.main.CanvaCord;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class CanvaCordScheduler {

	// A scheduler provided by Quartz
	private static Scheduler scheduler;

	/**
	 * Initialize the scheduler. This prepares Quartz to run
	 * scheduled Canvas fetches and Discord notifications.
	 * <br>
	 * If this fails, the application will exit immediately, as
	 * the scheduler is a critical component.
	 */
	public static void init() {

		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			CanvasFetchScheduler.init();
			NotificationScheduler.init();
			ReminderScheduler.init();
			scheduler.start();
		}
		catch (SchedulerException e) {
			e.printStackTrace();
			CanvaCord.explode(e.getMessage());
		}

	}

	/**
	 * Shut down the scheduler. This shutdown attempt will wait for
	 * any active jobs to complete before returning.
	 * @throws SchedulerException if something goes wrong shutting down the scheduler
	 */
	public static void shutDown() throws SchedulerException {
		scheduler.shutdown(true);
	}

	/**
	 * Add an Instance to the current scheduler. This will analyze the instance configuration
	 * to determine what jobs to run with what triggers, and add those into the Quartz scheduler.
	 * @param instance the Instance to schedule jobs for
	 * @throws SchedulerException if something goes wrong scheduling the Instance's jobs
	 */
	public static void scheduleInstance(Instance instance) throws SchedulerException {
		CanvasFetchScheduler.scheduleInstance(instance);
		NotificationScheduler.scheduleInstance(instance);
		ReminderScheduler.scheduleInstance(instance);
	}

	/**
	 * Remove an Instance from the scheduler. This removes any triggers for jobs associated
	 * with the given Instance.
	 * @param instance the Instance to no longer schedule jobs for
	 * @throws SchedulerException if something goes wrong terminating the Instance's jobs
	 */
	public static void removeInstance(Instance instance) throws SchedulerException {
		CanvasFetchScheduler.removeInstance(instance);
		NotificationScheduler.removeInstance(instance);
		ReminderScheduler.removeInstance(instance);
	}

}
