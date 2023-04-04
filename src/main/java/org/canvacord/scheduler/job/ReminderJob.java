package org.canvacord.scheduler.job;

import org.canvacord.instance.Instance;
import org.canvacord.reminder.Reminder;
import org.canvacord.reminder.ReminderManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ReminderJob implements Job {

	private Instance instance;
	private Reminder reminder;

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO
	}
}
