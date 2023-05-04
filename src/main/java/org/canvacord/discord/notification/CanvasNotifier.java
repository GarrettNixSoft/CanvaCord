package org.canvacord.discord.notification;

import edu.ksu.canvas.model.announcement.Announcement;
import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.gui.wizard.cards.instance.InstanceSetupWelcomeCard;
import org.canvacord.instance.Instance;
import org.canvacord.persist.CacheManager;
import org.canvacord.util.data.ListSplitter;
import org.canvacord.util.data.Pair;
import org.canvacord.util.string.StringConverter;
import org.canvacord.util.time.CanvaCordTime;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.permission.RoleBuilder;

import java.awt.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CanvasNotifier {

	/**
	 * Send notifications to users about newly posted assignments.
	 * @param notificationConfig a notification configured by the Owner which specifies
	 *                           the target channel, roles to ping, and message format
	 *                           to be used
	 * @param assignments a list of assignments found on Canvas
	 * @return {@code true} if all notifications are sent successfully, or false otherwise
	 */
	public static boolean notifyNewAssignments(Instance instance, CanvaCordNotification notificationConfig, List<Assignment> assignments) {

		// quit fast if there's nothing to send
		if (assignments.isEmpty())
			return true;

		try {
			// get discord api
			DiscordApi api = DiscordBot.getBotInstance().getApi();

			// text channel to send messages
			TextChannel channel = api.getTextChannelById(notificationConfig.getChannelID()).orElse(null);

			// Initialize message builder
			MessageBuilder messageBuilder = new MessageBuilder();

			// Who to send messages to
			AllowedMentions allowedMentions = new AllowedMentionsBuilder()
					.setMentionRoles(true)
					.setMentionEveryoneAndHere(false)
					.build();
			messageBuilder.setAllowedMentions(allowedMentions);

			// Append pings to the start of the message
			List<CanvaCordRole> rolesToPing = notificationConfig.getRolesToPing();
			AtomicInteger pingSizeAtomic = new AtomicInteger(0);
			for (CanvaCordRole canvaCordRole : rolesToPing) {
				MessageBuilder finalMessageBuilder = messageBuilder;
				api.getRoleById(canvaCordRole.getRoleID()).ifPresent(
						role -> {
							finalMessageBuilder.append(role.getMentionTag());
							pingSizeAtomic.set(pingSizeAtomic.get() + role.getMentionTag().length());
						}
				);
			}

			// Add newlines to move the body down
			messageBuilder.append("\n\n");
			int pingSize = pingSizeAtomic.get() + 2;

			// Start formatting the message
			String messageFormat = notificationConfig.getMessageFormat();
			int messageLimit = 2000 - pingSize;
			int totalLength = 0;

			List<String> assignmentMessages = new ArrayList<>();

			for (Assignment assignment : assignments) {
				if (assignment == null) continue;
				DateFormat dateFormat = CanvaCordTime.getDateFormat();
				String assignmentMessage = messageFormat;
				assignmentMessage = assignmentMessage.replace("${assignment.name}", assignment.getName());
				if (assignment.getDueAt() != null)
					assignmentMessage = assignmentMessage.replace("${assignment.due}", dateFormat.format(assignment.getDueAt()));
				else
					assignmentMessage = assignmentMessage.replace("${assignment.due}", "No Due Date");
				assignmentMessage = assignmentMessage.replace("${assignment.date}", dateFormat.format(assignment.getCreatedAt()));
				assignmentMessage = assignmentMessage.replace("${assignment.points}", String.format("%.2f", assignment.getPointsPossible()));
				assignmentMessages.add(assignmentMessage);
				totalLength += assignmentMessage.length();
			}

			// Check if we're under the limit
			if (totalLength < messageLimit) {
				String uberMessage = StringConverter.combineAllSeparatedBy(assignmentMessages, "\n");
				messageBuilder.setContent(uberMessage).send(channel).join();
			}
			else {
				// check divisors until we get a workable size
				int divisor = 2;
				while (totalLength / divisor >= messageLimit) divisor++;
				// print a warning if the divisor is greater than 5 (this will hit Discord's rate limit and slow things down a bunch)
				// split the list up into that many sublists
				List<String>[] splitAssignments = new ListSplitter<String>().splitListIntoSublists(assignmentMessages, divisor);
				int i = 0;
				do {
					List<String> thisBatch = splitAssignments[i];
					String uberMessage = StringConverter.combineAllSeparatedBy(thisBatch, "\n");
					messageBuilder.setContent(uberMessage).send(channel).join();
					messageBuilder = new MessageBuilder();
					messageBuilder.setAllowedMentions(allowedMentions);
					i++;
				} while (i < splitAssignments.length);
			}

			for (Assignment assignment : assignments) {
				if (assignment != null) {
					// MARK THIS AS SENT
					CacheManager.markSent(instance, notificationConfig, assignment);
				}
			}

			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Send notifications to users about changed due dates.
	 * @param notificationConfig a notification configured by the Owner which specifies
	 *                           the target channel, roles to ping, and message format
	 *                           to be used
	 * @param assignments the assignments whose due dates were changed
	 * @return {@code true} if the notification is sent successfully, or false otherwise
	 */
	public static boolean notifyDueDateChanged(Instance instance, CanvaCordNotification notificationConfig, List<Pair<Assignment, Pair<Date, Date>>> assignments) {

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
			if (assignment == null) continue;
			embedBuilder.addField(assignment.getName(), "New Due Date: " + datePair.second());
		}

		AllowedMentions allowedMentions = new AllowedMentionsBuilder()
				.setMentionRoles(true)
				.setMentionEveryoneAndHere(false)
				.build();

		List<CanvaCordRole> rolesToPing = notificationConfig.getRolesToPing();

		for(int i = 0; i < rolesToPing.size(); i++) {
			api.getRoleById(rolesToPing.get(i).getRoleID()).ifPresent(
					role -> messageBuilder.append(role.getMentionTag())
			);

		}

		messageBuilder
				.addEmbed(embedBuilder)
				.setAllowedMentions(allowedMentions)
				.send(channel);

		for (Pair<Assignment, Pair<Date, Date>> assignment : assignments) {
			if (assignment != null) {
				// MARK THIS AS SENT
				CacheManager.markSent(instance, notificationConfig, assignment.first());
			}
		}

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
	public static boolean notifyDueDateApproaching(Instance instance, CanvaCordNotification notificationConfig, List<Assignment> assignments) {
		DiscordApi api = DiscordBot.getBotInstance().getApi();
		TextChannel channel = api.getTextChannelById(notificationConfig.getChannelID()).orElse(null);
		MessageBuilder messageBuilder = new MessageBuilder();
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.YELLOW) // Set a different color to differentiate from new assignments and changed due dates
				.setTitle("Upcoming Assignments")
				.setDescription("The following assignments are due soon:");

		for (Assignment assignment : assignments) {
			if (assignment == null) continue;
			embedBuilder.addField(assignment.getName(), "Due Date: " + assignment.getDueAt());
		}

		AllowedMentions allowedMentions = new AllowedMentionsBuilder()
				.setMentionRoles(true)
				.setMentionEveryoneAndHere(false)
				.build();

		List<CanvaCordRole> rolesToPing = notificationConfig.getRolesToPing();

		for(int i = 0; i < rolesToPing.size(); i++) {
			api.getRoleById(rolesToPing.get(i).getRoleID()).ifPresent(
					role -> messageBuilder.append(role.getMentionTag())
			);

		}

		messageBuilder
				.addEmbed(embedBuilder)
				.setAllowedMentions(allowedMentions)
				.send(channel);

		for (Assignment assignment : assignments) {
			if (assignment != null) {
				// MARK THIS AS SENT
				CacheManager.markSent(instance, notificationConfig, assignment);
			}
		}

		return true;
	}

	public static boolean notifyNewAnnouncements(Instance instance, CanvaCordNotification notificationConfig, List<Announcement> announcements) {
		DiscordApi api = DiscordBot.getBotInstance().getApi();

		// Get the target channel for the notification
		TextChannel channel = api.getTextChannelById(notificationConfig.getChannelID()).orElse(null);

		// Initialize the message builder
		MessageBuilder messageBuilder = new MessageBuilder();

		// Create an embed builder for the message
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.GREEN)
				.setTitle("New Announcements Posted")
				.setDescription("New announcements have been posted on Canvas!");

		// Need to fix format for announcement message, also announcements dont contain to links
		for (Announcement announcement : announcements) {
			if (announcement == null) continue;
			embedBuilder.addField(announcement.getTitle(),
					"Posted at: "+announcement.getPostedAt().toString()+", "+announcement.getMessage());
		}
		AllowedMentions allowedMentions = new AllowedMentionsBuilder()
				.setMentionRoles(true)
				.setMentionEveryoneAndHere(false)
				.build();

		List<CanvaCordRole> rolesToPing = notificationConfig.getRolesToPing();

		for(int i = 0; i < rolesToPing.size(); i++) {
			api.getRoleById(rolesToPing.get(i).getRoleID()).ifPresent(
					role -> messageBuilder.append(role.getMentionTag())
			);

		}

		messageBuilder
				.addEmbed(embedBuilder)
				.setAllowedMentions(allowedMentions)
				.send(channel);

		for (Announcement announcement : announcements) {
			if (announcement != null) {
				// MARK THIS AS SENT
				CacheManager.markSent(instance, notificationConfig, announcement);
			}
		}

		return true;
	}

}
