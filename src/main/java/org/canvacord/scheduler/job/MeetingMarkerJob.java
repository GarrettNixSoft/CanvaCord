package org.canvacord.scheduler.job;

import org.canvacord.instance.Instance;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MeetingMarkerJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap = context.getMergedJobDataMap();
		Instance instance = (Instance) dataMap.get("instance");



		// TODO
	}

}
