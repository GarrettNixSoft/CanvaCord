package org.canvacord.scheduler.job;

import org.canvacord.instance.InstanceManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CanvasFetchJob implements Job {

    private String instanceID;

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        System.out.println("Fetching Canvas data for instance " + instanceID);
        InstanceManager.updateInstance(instanceID);

    }
}
