package org.canvacord.discord.commands;

import org.canvacord.instance.Instance;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashMap;
import java.util.Map;

public abstract class Command {
	public abstract String getDescription();
	public abstract String getShortDescription(); // "need" this for the command builder/ can be changed
	public abstract String getName();
	public abstract void execute(SlashCommandInteraction interaction);

	protected abstract SlashCommandBuilder getBuilder(Instance instance);

	public static final Map<String, Class<? extends Command>> COMMANDS_BY_NAME = new HashMap<>();

	static {
		COMMANDS_BY_NAME.put("help", HelpCommand.class);
		COMMANDS_BY_NAME.put("remindme", RemindMeCommand.class);
	}

}
