package org.canvacord.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class NotificationJob implements Job {

    private long serverID;

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        System.out.println("Sending notifications to server " + serverID);

    }

}
