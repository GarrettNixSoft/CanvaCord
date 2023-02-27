package org.canvacord.discord.commands;

import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

public abstract class Command {
	public abstract String getDescription();
	public abstract String getShortDescription(); // "need" this for the command builder/ can be changed
	public abstract String getName();
	public abstract void execute(SlashCommandInteraction interaction);

	protected abstract SlashCommandBuilder getBuilder();

}
