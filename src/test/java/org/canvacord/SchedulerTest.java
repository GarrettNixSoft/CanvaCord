package org.canvacord;

import org.canvacord.instance.Instance;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.canvacord.scheduler.NotificationScheduler;

public class SchedulerTest {

    public static void main(String[] args) {

        try {
            Instance instance = new Instance("32202", 1016848330992656415L);

            CanvaCordScheduler.init();
            CanvaCordScheduler.scheduleInstance(instance);

            Thread.sleep(1000 * 60 * 60);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
