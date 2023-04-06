package org.canvacord.discord.commands;

import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.reminder.Reminder;
import org.canvacord.reminder.ReminderEncryption;
import org.canvacord.reminder.ReminderManager;
import org.canvacord.util.time.CanvaCordTime;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RemindMeCommand extends Command {

	@Override
	public String getDescription() {
		return "Set a custom reminder at the time you specify with the message you provide.";
	}

	@Override
	public String getShortDescription() {
		return "Set a custom reminder.";
	}

	@Override
	public String getName() {
		return "remindme";
	}

	@Override
	public void execute(SlashCommandInteraction interaction) {

		// Prepare to respond with common parameters
		InteractionImmediateResponseBuilder responseBuilder = interaction.createImmediateResponder();
		responseBuilder.setFlags(MessageFlag.EPHEMERAL);

		// Check the Instance context for this command
		Optional<Server> server = interaction.getServer();
		if (server.isEmpty()) {
			responseBuilder.setContent("Could not fetch the server context. Please contact the server Owner.").respond();
			return;
		}

		Optional<Instance> instanceOpt = InstanceManager.getInstanceByServerID(server.get().getId());
		if (instanceOpt.isEmpty()) {
			responseBuilder.setContent("Could not fetch the CanvaCord instance for this server. Please contact the server Owner.").respond();
			return;
		}

		Instance instance = instanceOpt.get();

		// Check that this instance allows reminders
		if (!instance.doCustomReminders()) {
			responseBuilder.setContent("This server does not allow custom reminders.").respond();
			return;
		}

		// Grab the time value
		Optional<SlashCommandInteractionOption> valueOption = interaction.getOptionByIndex(0);
		if (valueOption.isEmpty()) {
			responseBuilder.setContent("Missing time value.").respond();
			return;
		}

		// Validate the data type
		Optional<Long> longOption = valueOption.get().getLongValue();
		if (longOption.isEmpty()) {
			responseBuilder.setContent("Bad time value data type.").respond();
			return;
		}

		// Grab the unit value
		Optional<SlashCommandInteractionOption> unitOption = interaction.getOptionByIndex(1);
		if (unitOption.isEmpty()) {
			responseBuilder.setContent("Missing unit value.").respond();
			return;
		}

		// Validate the data type
		Optional<String> unitString = unitOption.get().getStringValue();
		if (unitString.isEmpty()) {
			responseBuilder.setContent("Bad unit value data type.").respond();
			return;
		}

		// Grab the encryption flag
		Optional<SlashCommandInteractionOption> encryptOption = interaction.getOptionByIndex(2);
		if (encryptOption.isEmpty()) {
			responseBuilder.setContent("Missing encrypt flag.").respond();
			return;
		}

		// Validate the data type
		Optional<Boolean> encrypt = encryptOption.get().getBooleanValue();
		if (encrypt.isEmpty()) {
			responseBuilder.setContent("Bad encrypt flag data type.").respond();
			return;
		}

		// Grab the message value
		Optional<SlashCommandInteractionOption> messageOption = interaction.getOptionByIndex(3);
		if (messageOption.isPresent()) {
			// validate the data type
			if (messageOption.get().getStringValue().isEmpty()) {
				responseBuilder.setContent("Bad message data type.").respond();
				return;
			}
		}

		// Grab the user's ID
		User user = interaction.getUser();

		// Grab the channel ID
		Optional<TextChannel> channelOpt = interaction.getChannel();
		if (channelOpt.isEmpty()) {
			responseBuilder.setContent("Could not get the channel context. Please contact the server Owner.").respond();
			return;
		}
		TextChannel channel = channelOpt.get();

		// Generate a reminder based on the validated data
		long timeValue = longOption.get();
		String unit = unitString.get();
		String message = messageOption.map(msg -> msg.getStringValue().get()).orElse(getDefaultMessage(interaction));

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime reminderTime = switch (unit) {
			case "days" -> now.plusDays(timeValue);
			case "weeks" -> now.plusWeeks(timeValue);
			case "hours" -> now.plusHours(timeValue);
			default -> now.plusMinutes(timeValue);
		};

		// Build a reminder
		Reminder reminder = Reminder.buildNew(user.getId(), channel.getId(), reminderTime, message);

		// Encrypt if requested
		if (encrypt.get())
			reminder = ReminderEncryption.encryptReminder(reminder);

		// Register the reminder
		ReminderManager.addNewReminder(instance, reminder);

		// Respond to the user with a success confirmation
		responseBuilder.setContent("Reminder created for " + CanvaCordTime.getFriendlyDateString(reminderTime)).respond();

	}

	@Override
	public SlashCommandBuilder getBuilder() {
		return SlashCommand.with(
			getName(),
			getDescription(),
			List.of(
				SlashCommandOption.create(
					SlashCommandOptionType.LONG,
					"time",
					"How many units of time to wait",
					true
				),
				SlashCommandOption.createWithChoices(
					SlashCommandOptionType.STRING,
					"unit",
					"The time unit to use",
					true,
					List.of(
						SlashCommandOptionChoice.create(
							"Days",
							"days"
						),
						SlashCommandOptionChoice.create(
							"Hours",
							"hours"
						),
						SlashCommandOptionChoice.create(
							"Weeks",
							"weeks"
						),
						SlashCommandOptionChoice.create(
							"Minutes",
							"minutes"
						)
					)
				),
				SlashCommandOption.create(
					SlashCommandOptionType.BOOLEAN,
					"encrypted",
					"Whether to encrypt your reminder message.",
					true
				),
				SlashCommandOption.create(
					SlashCommandOptionType.STRING,
					"message",
					"Reminder message; blank = a link to the most recent message in this channel.",
					false
				)
			)
		);
	}

	private String getDefaultMessage(SlashCommandInteraction interaction) {
		// must be able to grab the channel
		Optional<TextChannel> channelOpt = interaction.getChannel();
		if (channelOpt.isEmpty()) return "Channel ID not found";
		TextChannel channel = channelOpt.get();
		// Must be able to find a recent message
		MessageSet recentMessages = channel.getMessages(1).join();
		if (recentMessages.isEmpty()) return "No recent messages to link to";
		// Attach to the most recent message
		Optional<Message> messageOpt = recentMessages.getNewestMessage();
		if (messageOpt.isEmpty()) return "Could not find most recent message";
		Message recentMessage = messageOpt.get();
		return recentMessage.getLink().toString();
	}

}
