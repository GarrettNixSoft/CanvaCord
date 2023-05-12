package org.canvacord.scheduler.job;

import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.instance.Instance;
import org.canvacord.util.string.StringUtils;
import org.canvacord.util.time.CanvaCordTime;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MeetingMarkerJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap = context.getMergedJobDataMap();
		Instance instance = (Instance) dataMap.get("instance");
		ClassMeeting meeting = (ClassMeeting) dataMap.get("meeting");
		String type = StringUtils.uppercaseWords(dataMap.getString("type"));
		long channelID = instance.getMeetingRemindersChannel();
		long roleID = instance.getMeetingRemindersRole();

		DiscordApi api = DiscordBot.getBotInstance().getApi();
		ServerTextChannel channel = api.getTextChannelById(channelID).get().asServerTextChannel().get();

		MessageBuilder messageBuilder = new MessageBuilder();

		if (instance.createRemindersRole() && roleID != -1) {
			// Who to send messages to
			AllowedMentions allowedMentions = new AllowedMentionsBuilder()
					.setMentionRoles(true)
					.setMentionEveryoneAndHere(false)
					.build();
			messageBuilder.setAllowedMentions(allowedMentions);
			messageBuilder.append(api.getRoleById(roleID));
		}

		String timeString = type.equals("Start") ? meeting.getStartDescription() : meeting.getEndDescription();

		messageBuilder.setContent(" " + type + " of meeting for " + CanvaCordTime.getTodayString() + " at " + timeString);
		messageBuilder.send(channel);

	}

}
