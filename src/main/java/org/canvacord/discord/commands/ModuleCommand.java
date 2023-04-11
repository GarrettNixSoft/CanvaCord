package org.canvacord.discord.commands;

import org.canvacord.instance.Instance;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ModuleCommand extends Command {
	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getShortDescription() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void execute(SlashCommandInteraction interaction) {
		// TODO
	}

	@Override
	protected SlashCommandBuilder getBuilder(Instance instance) {
		return null;
	}
}
