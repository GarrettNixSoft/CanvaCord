package org.canvacord.scheduler;

import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.CanvasFetchJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

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
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(instance.getInstanceID(), GROUP_ID)
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(30)
                                .repeatForever()
                ).build();

        fetchScheduler.scheduleJob(fetchJob, trigger);

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

}
