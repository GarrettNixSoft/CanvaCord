package org.canvacord.scheduler;

import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.NotificationJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class NotificationScheduler {

    private static Scheduler notifyScheduler;
    private static final String GROUP_ID = "discordNotify";

    public static void init() throws SchedulerException {

        notifyScheduler = StdSchedulerFactory.getDefaultScheduler();

        System.out.println("Notification Scheduler initialized");

    }

    public static void scheduleInstance(Instance instance) {

        try {
            // TODO
            JobDetail notifyJob = JobBuilder.newJob(NotificationJob.class)
                    .withIdentity(instance.getInstanceID(), GROUP_ID)
                    .usingJobData("serverID", instance.getServerID())
                    .build();

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
        catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    public static void removeInstance(Instance instance) throws SchedulerException {
        JobKey key = new JobKey(instance.getInstanceID(), GROUP_ID);
        notifyScheduler.deleteJob(key);
    }

}
