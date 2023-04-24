package org.canvacord.discord.notification;

import edu.ksu.canvas.model.announcement.Announcement;
import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.util.data.Pair;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.permission.RoleBuilder;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CanvasNotifier {

	/**
	 * Send notifications to users about newly posted assignments.
	 * @param notificationConfig a notification configured by the Owner which specifies
	 *                           the target channel, roles to ping, and message format
	 *                           to be used
	 * @param assignments a list of assignments found on Canvas
	 * @return {@code true} if all notifications are sent successfully, or false otherwise
	 */
	public static boolean notifyNewAssignments(CanvaCordNotification notificationConfig, List<Assignment> assignments) {

		// get discord api
		DiscordApi api = DiscordBot.getBotInstance().getApi();

		// text channel to send messages
		TextChannel channel = api.getTextChannelById(notificationConfig.getChannelID()).orElse(null);

		// Initialize message builder
		MessageBuilder messageBuilder = new MessageBuilder();

		// Create an embed builder for the message
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.GREEN)
				.setTitle("New Assignments Posted")
				.setDescription("New assignments have been posted on Canvas!");

		// Add each assignment to the embed
		for (Assignment assignment : assignments) {
			embedBuilder.addField(assignment.getName(), "Due Date: " + assignment.getDueAt());
		}

		// Who to send messages to
		AllowedMentions allowedMentions = new AllowedMentionsBuilder()
				.setMentionRoles(true)
				.setMentionEveryoneAndHere(false)
				.build();

		List<CanvaCordRole> rolesToPing = notificationConfig.getRolesToPing();

		for(int i = 0; i < rolesToPing.size(); i++) {
			Role role = api.getRoleById(rolesToPing.get(i).getRoleID()).get();
			messageBuilder
					.append(role.getMentionTag());

		}

		// add to message builder
		messageBuilder
				.addEmbed(embedBuilder)
				.setAllowedMentions(allowedMentions)
				.send(channel);

		return true;
	}

	/**
	 * Send notifications to users about changed due dates.
	 * @param notificationConfig a notification configured by the Owner which specifies
	 *                           the target channel, roles to ping, and message format
	 *                           to be used
	 * @param assignments the assignments whose due dates were changed
	 * @return {@code true} if the notification is sent successfully, or false otherwise
	 */
	public static boolean notifyDueDateChanged(CanvaCordNotification notificationConfig, List<Pair<Assignment, Pair<Date, Date>>> assignments) {

		DiscordApi api = DiscordBot.getBotInstance().getApi();
		TextChannel channel = api.getTextChannelById(notificationConfig.getChannelID()).orElse(null);
		MessageBuilder messageBuilder = new MessageBuilder();
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.ORANGE) // Set a different color to differentiate from new assignments
				.setTitle("Due Dates Changed")
				.setDescription("Due dates for the following assignments have been updated:");

		for (Pair<Assignment, Pair<Date, Date>> assignmentPair : assignments) {
			Assignment assignment = assignmentPair.first();
			Pair<Date, Date> datePair = assignmentPair.second();
			embedBuilder.addField(assignment.getName(), "New Due Date: " + datePair.second());
		}

		AllowedMentions allowedMentions = new AllowedMentionsBuilder()
				.setMentionRoles(true)
				.setMentionEveryoneAndHere(false)
				.build();

		List<CanvaCordRole> rolesToPing = notificationConfig.getRolesToPing();

		for(int i = 0; i < rolesToPing.size(); i++) {
			Role role = api.getRoleById(rolesToPing.get(i).getRoleID()).get();
			messageBuilder.append(role.getMentionTag());
		}

		messageBuilder
				.addEmbed(embedBuilder)
				.setAllowedMentions(allowedMentions)
				.send(channel);

		return true;
	}

	/**
	 * Send notifications to users about approaching assignment due dates.
	 * @param notificationConfig a notification configured by the Owner which specifies
	 *                           the target channel, roles to ping, and message format
	 *                           to be used
	 * @param assignments the assignment that is due soon
	 * @return {@code true} if the notification is sent successfully, or false otherwise
	 */
	public static boolean notifyDueDateApproaching(CanvaCordNotification notificationConfig, List<Assignment> assignments) {
		DiscordApi api = DiscordBot.getBotInstance().getApi();
		TextChannel channel = api.getTextChannelById(notificationConfig.getChannelID()).orElse(null);
		MessageBuilder messageBuilder = new MessageBuilder();
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.YELLOW) // Set a different color to differentiate from new assignments and changed due dates
				.setTitle("Upcoming Assignments")
				.setDescription("The following assignments are due soon:");

		for (Assignment assignment : assignments) {
			embedBuilder.addField(assignment.getName(), "Due Date: " + assignment.getDueAt());
		}

		AllowedMentions allowedMentions = new AllowedMentionsBuilder()
				.setMentionRoles(true)
				.setMentionEveryoneAndHere(false)
				.build();

		List<CanvaCordRole> rolesToPing = notificationConfig.getRolesToPing();

		for(int i = 0; i < rolesToPing.size(); i++) {
			Role role = api.getRoleById(rolesToPing.get(i).getRoleID()).get();
			messageBuilder.append(role.getMentionTag());
		}

		messageBuilder
				.addEmbed(embedBuilder)
				.setAllowedMentions(allowedMentions)
				.send(channel);

		return true;
	}

	public static boolean notifyNewAnnouncements(CanvaCordNotification notification, List<Announcement> announcements) {
		DiscordApi api = DiscordBot.getBotInstance().getApi();

		// Get the target channel for the notification
		TextChannel channel = api.getTextChannelById(notification.getChannelID()).orElse(null);

		// Initialize the message builder
		MessageBuilder messageBuilder = new MessageBuilder();

		// Create an embed builder for the message
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.GREEN)
				.setTitle("New Announcements Posted")
				.setDescription("New announcements have been posted on Canvas!");

		// Need to fix format for announcement message, also announcements dont contain to links
		for (Announcement announcement : announcements) {
			embedBuilder.addField(announcement.getTitle(),
					"Posted at: "+announcement.getPostedAt().toString()+", "+announcement.getMessage());
		}
		AllowedMentions allowedMentions = new AllowedMentionsBuilder()
				.setMentionRoles(true)
				.setMentionEveryoneAndHere(false)
				.build();

		List<CanvaCordRole> rolesToPing = notification.getRolesToPing();

		for(int i = 0; i < rolesToPing.size(); i++) {
			Role role = api.getRoleById(rolesToPing.get(i).getRoleID()).get();
			messageBuilder.append(role.getMentionTag());
		}

		messageBuilder
				.addEmbed(embedBuilder)
				.setAllowedMentions(allowedMentions)
				.send(channel);
		return true;
	}

}
