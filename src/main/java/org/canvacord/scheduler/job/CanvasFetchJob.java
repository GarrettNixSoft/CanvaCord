package org.canvacord.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CanvasFetchJob implements Job {

    private String courseID;

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // TODO
        System.out.println("Fetching Canvas data for course " + courseID);

    }
}
