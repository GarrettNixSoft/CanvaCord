package org.canvacord.discord.commands;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.InstanceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

	private static final Map<Long, Command> globalCommands = new HashMap<>();
	private static final Map<String, Map<Long, Command>> serverCommands = new HashMap<>();

	public static long registerCommandGlobal(Class<? extends Command> commandType, DiscordApi api) {
		try {
			Command commandObj = commandType.getConstructor().newInstance();
			SlashCommand globalCommand = commandObj.getBuilder().createGlobal(api).join();
			globalCommands.put(globalCommand.getId(), commandObj);
			return globalCommand.getId();
		}
		catch (Exception e) {
			throw new CanvaCordException(e.getMessage());
		}
	}

	public static long registerCommandServer(Class<? extends Command> commandType, Server server) {
		try {
			String instanceID = InstanceManager.getInstanceByServerID(server.getId()).get().getInstanceID();
			Command commandObj = commandType.getConstructor().newInstance();
			SlashCommand serverCommand = commandObj.getBuilder().createForServer(server).join();
			serverCommands.get(instanceID).put(serverCommand.getId(), commandObj);
			return serverCommand.getId();
		}
		catch (Exception e) {
			throw new CanvaCordException(e.getMessage());
		}
	}

	public static void executeCommand(SlashCommandInteraction interaction) {
		String instanceID = InstanceManager.getInstanceByServerID(interaction.getServer().get().getId()).get().getInstanceID();
		long commandID = interaction.getCommandId();
		if (serverCommands.get(instanceID).containsKey(commandID)) {
			serverCommands.get(instanceID).get(commandID).execute(interaction);
		}
		else {
			// Unregistered command!
			throw new RuntimeException();
		}
	}

}
