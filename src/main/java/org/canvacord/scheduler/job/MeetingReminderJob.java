package org.canvacord.scheduler.job;

import org.canvacord.discord.DiscordBot;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MeetingReminderJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap = context.getMergedJobDataMap();
		Instance instance = InstanceManager.getInstanceByID(dataMap.getString("instanceID")).get();
		int reminderSchedule = instance.getClassReminderSchedule();
		long channelID = instance.getMeetingRemindersChannel();
		long roleID = instance.getMeetingRemindersRole();

		DiscordApi api = DiscordBot.getBotInstance().getApi();
		ServerTextChannel channel = api.getTextChannelById(channelID).get().asServerTextChannel().get();

		MessageBuilder messageBuilder = new MessageBuilder();

		if (instance.createMarkersRole() && roleID != -1) {
			// Who to send messages to
			AllowedMentions allowedMentions = new AllowedMentionsBuilder()
					.setMentionRoles(true)
					.setMentionEveryoneAndHere(false)
					.build();
			messageBuilder.setAllowedMentions(allowedMentions);
			messageBuilder.append(api.getRoleById(roleID));
		}

		messageBuilder.setContent(" The next class meeting begins in " + reminderSchedule + " minutes!");
		messageBuilder.send(channel);

	}

}
