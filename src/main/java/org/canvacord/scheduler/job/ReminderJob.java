package org.canvacord.scheduler.job;

import org.canvacord.discord.DiscordBot;
import org.canvacord.instance.Instance;
import org.canvacord.reminder.Reminder;
import org.canvacord.reminder.ReminderManager;
import org.canvacord.util.time.CanvaCordTime;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Optional;

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

		// Grab the required objects for this job
		instance = (Instance) context.getMergedJobDataMap().get("instance");
		reminder = (Reminder) context.getMergedJobDataMap().get("reminder");

		// Fetch the Discord API
		DiscordApi discordApi = DiscordBot.getBotInstance().getApi();

		// Make sure we can access the target channel
		Optional<ServerTextChannel> targetChannelOpt = discordApi.getServerTextChannelById(reminder.channelID());
		if (targetChannelOpt.isEmpty()) throw new JobExecutionException("Could not get channel!");

		// Fetch the target user
		User targetUser = discordApi.getUserById(reminder.userID()).join();

		// Build an embed
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("Reminder");
		embedBuilder.addField("Created At", CanvaCordTime.getFriendlyDateString(reminder.createdAt()));
		embedBuilder.addField("Message", reminder.message());

		// Send the embed to the user
		targetUser.sendMessage(embedBuilder);

		// TODO anything else?

		// Remove this reminder from the instance data
		ReminderManager.registerReminderSent(instance, reminder);

	}
}
