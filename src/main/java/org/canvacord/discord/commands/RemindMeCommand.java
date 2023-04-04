package org.canvacord.discord.commands;

import org.javacord.api.interaction.*;

import java.util.List;

public class RemindMeCommand extends Command {

	@Override
	public String getDescription() {
		return "Set a custom reminder, which will be sent to you at the time you specify with the message you provide.";
	}

	@Override
	public String getShortDescription() {
		return "Set a custom reminder.";
	}

	@Override
	public String getName() {
		return "Remind Me";
	}

	@Override
	public void execute(SlashCommandInteraction interaction) {
		// TODO
	}

	@Override
	protected SlashCommandBuilder getBuilder() {
		return SlashCommand.with(
			getName(),
			getDescription(),
			List.of(
				SlashCommandOption.create(
					SlashCommandOptionType.LONG,
					"Time",
					"Number of time units in the future",
					true
				),
				SlashCommandOption.createWithOptions(
					SlashCommandOptionType.SUB_COMMAND_GROUP,
					"Unit",
					"Unit of time to use",
					List.of(
						SlashCommandOption.createWithOptions(
							SlashCommandOptionType.SUB_COMMAND,
							"Weeks",
							"Use Weeks as your time unit.",
							buildSubOptions()
						),
						SlashCommandOption.createWithOptions(
							SlashCommandOptionType.SUB_COMMAND,
							"Days",
							"Use Days as your time unit.",
							buildSubOptions()
						),
						SlashCommandOption.createWithOptions(
							SlashCommandOptionType.SUB_COMMAND,
							"Hours",
							"Use Hours as your time unit.",
							buildSubOptions()
						),
						SlashCommandOption.createWithOptions(
							SlashCommandOptionType.SUB_COMMAND,
							"Minutes",
							"Use Minutes as your time unit.",
							buildSubOptions()
						)
					)
				)
			)
		);
	}

	private List<SlashCommandOption> buildSubOptions() {
		return List.of(
				SlashCommandOption.create(
						SlashCommandOptionType.STRING,
						"Message",
						"Your reminder message."
				)
		);
	}

}
