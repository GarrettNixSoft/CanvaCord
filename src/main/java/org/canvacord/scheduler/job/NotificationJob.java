package org.canvacord.scheduler.job;

import org.canvacord.entity.CanvaCordNotification;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class NotificationJob implements Job {

    private long serverID;
    private CanvaCordNotification notification;

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public void setCanvaCordNotification(CanvaCordNotification notification) {
        this.notification = notification;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        System.out.println("Sending notifications to server " + serverID);

    }

}
