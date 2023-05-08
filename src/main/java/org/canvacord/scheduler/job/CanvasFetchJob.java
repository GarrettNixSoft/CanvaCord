package org.canvacord.scheduler.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.instance.InstanceManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CanvasFetchJob implements Job {

    private static final Logger LOGGER = LogManager.getLogger();

    private String instanceID;

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        LOGGER.debug("Fetching Canvas data for instance " + instanceID);
        InstanceManager.updateInstance(instanceID);

    }
}
