package org.canvacord.discord.commands;

import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashMap;
import java.util.Map;

public abstract class Command {
	public abstract String getDescription();
	public abstract String getShortDescription(); // "need" this for the command builder/ can be changed
	public abstract String getName();
	public abstract void execute(SlashCommandInteraction interaction);

	protected abstract SlashCommandBuilder getBuilder();

	public static final Map<String, Class<? extends Command>> COMMAND_NAMES = new HashMap<>();

	static {
		COMMAND_NAMES.put("help", HelpCommand.class);
		COMMAND_NAMES.put("remindme", RemindMeCommand.class);
		COMMAND_NAMES.put("modulelist", ModuleCommand.class);
	}

}
