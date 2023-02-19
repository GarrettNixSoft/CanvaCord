package org.canvacord.scheduler;

import org.canvacord.instance.Instance;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class CanvaCordScheduler {

    private static Scheduler scheduler;

    public static void init() {

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            CanvasFetchScheduler.init();
            NotificationScheduler.init();
            scheduler.start();
        }
        catch (SchedulerException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public static void shutDown() throws SchedulerException {

        try {
            scheduler.shutdown(true);
        }
        catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    public static void scheduleInstance(Instance instance) throws SchedulerException {
        CanvasFetchScheduler.scheduleInstance(instance);
        NotificationScheduler.scheduleInstance(instance);
    }

    public static void removeInstance(Instance instance) throws SchedulerException {
        CanvasFetchScheduler.removeInstance(instance);
        NotificationScheduler.removeInstance(instance);
    }

}
