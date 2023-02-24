package org.canvacord.discord.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

	private static final Map<Long, Command> commands = new HashMap<>();

	public static long registerCommandGlobal(SlashCommandBuilder slashCommand, Command command, DiscordApi api) {
		SlashCommand globalCommand = slashCommand.createGlobal(api).join();
		commands.put(globalCommand.getId(), command);
		return globalCommand.getId();
	}

	public static long registerCommandServer(SlashCommandBuilder slashCommand, Command command, Server server) {
		SlashCommand serverCommand = slashCommand.createForServer(server).join();
		commands.put(serverCommand.getId(), command);
		return serverCommand.getId();
	}

	public static void executeCommand(SlashCommandInteraction interaction) {
		long commandID = interaction.getCommandId();
		if (commands.containsKey(commandID)) {
			commands.get(commandID).execute(interaction);
		}
		else {
			// Unregistered command!
			throw new RuntimeException();
		}
	}

}
