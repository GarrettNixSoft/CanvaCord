package org.canvacord.discord.commands;

import org.canvacord.exception.CanvaCordException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

	private static final Map<Long, Command> commands = new HashMap<>();

	public static long registerCommandGlobal(Class<? extends Command> commandType, DiscordApi api) {
		try {
			Command commandObj = commandType.getConstructor().newInstance();
			SlashCommand globalCommand = commandObj.getBuilder().createGlobal(api).join();
			commands.put(globalCommand.getId(), commandObj);
			return globalCommand.getId();
		}
		catch (Exception e) {
			throw new CanvaCordException(e.getMessage());
		}
	}

	public static long registerCommandServer(Class<? extends Command> commandType, Server server) {
		try {
			Command commandObj = commandType.getConstructor().newInstance();
			SlashCommand serverCommand = commandObj.getBuilder().createForServer(server).join();
			commands.put(serverCommand.getId(), commandObj);
			return serverCommand.getId();
		}
		catch (Exception e) {
			throw new CanvaCordException(e.getMessage());
		}
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
