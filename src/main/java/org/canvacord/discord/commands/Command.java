package org.canvacord.discord.commands;

import org.javacord.api.interaction.SlashCommandInteraction;

public abstract class Command {
	public abstract String getDescription();
	//FROM IAN: feel free to change these if it gets in the way, just LMK so I can fix the HELP command
	public abstract String getName();
	public abstract void execute(SlashCommandInteraction interaction);

}
