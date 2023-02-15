package org.canvacord.scheduler;

import org.canvacord.instance.Instance;
import org.canvacord.scheduler.job.CanvasFetchJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleJobFactory;

public class CanvasFetchScheduler {

	private static Scheduler fetchScheduler;

    public static void init() throws SchedulerException {

        fetchScheduler = StdSchedulerFactory.getDefaultScheduler();

        System.out.println("Canvas Fetch Scheduler initialized");

    }

    public static void shutDown() throws SchedulerException {

        fetchScheduler.shutdown();

    }

    public static void scheduleInstance(Instance instance) {

        try {
            // TODO
            JobDetail fetchJob = JobBuilder.newJob(CanvasFetchJob.class)
                    .withIdentity(instance.getInstanceID(), "canvasFetch")
                    .usingJobData("courseID", instance.getCourseID())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(instance.getInstanceID(), "canvasFetch")
                    .startNow()
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(10)
                                    .repeatForever()
                    ).build();

            fetchScheduler.scheduleJob(fetchJob, trigger);

            System.out.println("Scheduled updates for instance " + instance.getInstanceID());
        }
        catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    public static void removeInstance(Instance instance) {

        // TODO

    }

}
