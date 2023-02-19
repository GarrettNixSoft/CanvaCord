package org.canvacord.scheduler;

import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.CanvasFetchJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleJobFactory;

public class CanvasFetchScheduler {

	private static Scheduler fetchScheduler;
    private static final String GROUP_ID = "canvasFetch";

    public static void init() throws SchedulerException {

        fetchScheduler = StdSchedulerFactory.getDefaultScheduler();

        System.out.println("Canvas Fetch Scheduler initialized");

    }

    public static void scheduleInstance(Instance instance) throws SchedulerException {

        JobDetail fetchJob = JobBuilder.newJob(CanvasFetchJob.class)
                .withIdentity(instance.getInstanceID(), GROUP_ID)
                .usingJobData("courseID", instance.getCourseID())
                .build();

        // TODO make this dynamic based on instance config
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(instance.getInstanceID(), GROUP_ID)
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(10)
                                .repeatForever()
                ).build();

        fetchScheduler.scheduleJob(fetchJob, trigger);

        System.out.println("Scheduled updates for instance " + instance.getInstanceID());

    }

    public static void removeInstance(Instance instance) throws SchedulerException {
        JobKey key = new JobKey(instance.getInstanceID(), GROUP_ID);
        fetchScheduler.deleteJob(key);
    }

}
