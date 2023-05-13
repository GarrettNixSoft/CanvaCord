package org.canvacord.scheduler.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap = context.getMergedJobDataMap();
		Instance instance = (Instance) dataMap.get("instance");
		ClassMeeting meeting = (ClassMeeting) dataMap.get("meeting");
		String type = StringUtils.uppercaseWords(dataMap.getString("type"));
		long channelID = instance.getMeetingRemindersChannel();
		long roleID = instance.getMeetingRemindersRole();

		LOGGER.debug("Meeting marker role ID: " + roleID);

		DiscordApi api = DiscordBot.getBotInstance().getApi();
		ServerTextChannel channel = api.getTextChannelById(channelID).get().asServerTextChannel().get();

		MessageBuilder messageBuilder = new MessageBuilder();

		if (instance.createRemindersRole() && roleID != -1) {
			// Who to send messages to
			AllowedMentions allowedMentions = new AllowedMentionsBuilder()
					.setMentionRoles(true)
					.setMentionEveryoneAndHere(false)
					.addRole(roleID)
					.build();
			messageBuilder.setAllowedMentions(allowedMentions);
			messageBuilder.append(api.getRoleById(roleID).get().getMentionTag());
//			LOGGER.debug("Appended role to Meeting Marker message");
		}

		String timeString = type.equals("Start") ? meeting.getStartDescription() : meeting.getEndDescription();

		messageBuilder.append(" " + type + " of meeting for " + CanvaCordTime.getTodayString() + " at " + timeString);
		messageBuilder.send(channel);

	}

}
