package org.canvacord.scheduler;

import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.NotificationJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class NotificationScheduler {

    private static Scheduler notifyScheduler;
    private static final String GROUP_ID = "discordNotify";

    /**
     * Initialize the Notification scheduler. This prepares it for
     * scheduling notification jobs for Instances.
     * @throws SchedulerException if something goes wrong getting the default scheduler
     */
    public static void init() throws SchedulerException {

        notifyScheduler = StdSchedulerFactory.getDefaultScheduler();

        System.out.println("Notification Scheduler initialized");

    }

    /**
     * Schedule all notification jobs for the given Instance.
     * @param instance if something goes wrong registering the jobs
     */
    public static void scheduleInstance(Instance instance) throws SchedulerException {

        // Build a job for sending notifications
        JobDetail notifyJob = JobBuilder.newJob(NotificationJob.class)
                .withIdentity(instance.getInstanceID(), GROUP_ID)
                .usingJobData("serverID", instance.getServerID())
                .build();

        // Build a trigger for the job
        // TODO make this dynamic based on instance config
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(instance.getInstanceID(), GROUP_ID)
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(15)
                                .repeatForever()
                ).build();

        notifyScheduler.scheduleJob(notifyJob, trigger);

        System.out.println("Scheduled notifications for instance " + instance.getInstanceID());

    }

    /**
     * Remove an Instance's notification jobs from the scheduler.
     * @param instance the Instance to remove
     * @throws SchedulerException if something goes wrong removing the Instance's jobs
     */
    public static void removeInstance(Instance instance) throws SchedulerException {
        JobKey key = new JobKey(instance.getInstanceID(), GROUP_ID);
        notifyScheduler.deleteJob(key);
    }

}
