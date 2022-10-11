package org.canvacord.discord.commands;

import org.javacord.api.interaction.SlashCommandInteraction;

public abstract class Command {

	public abstract void execute(SlashCommandInteraction interaction);

}
