package org.canvacord.discord.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

	private static final Logger LOGGER = LogManager.getLogger(CommandHandler.class);

	private static final Map<Long, Class<? extends Command>> globalCommands = new HashMap<>();
	private static final Map<String, Map<Long, Class<? extends Command>>> serverCommands = new HashMap<>();

	/**
	 * Initialize the command handler to listen for commands in different servers.
	 */
	public static void init() {
		// TODO global command: help
		// Load all commands per instance
		for (Instance instance : InstanceManager.getInstances()) {
			serverCommands.put(instance.getInstanceID(), instance.getRegisteredCommands());
		}
	}

	public static long registerCommandGlobal(Class<? extends Command> commandType, DiscordApi api) {
		try {
			Command commandObj = commandType.getConstructor().newInstance();
			SlashCommand globalCommand = commandObj.getBuilder(null).createGlobal(api).join();
			globalCommands.put(globalCommand.getId(), commandType);
			return globalCommand.getId();
		}
		catch (Exception e) {
			throw new CanvaCordException(e.getMessage());
		}
	}

	public static long registerCommandServer(Class<? extends Command> commandType, Server server) {
		try {
			Instance instance = InstanceManager.getInstanceByServerID(server.getId()).get();
			String instanceID = instance.getInstanceID();
			Command commandObj = commandType.getConstructor().newInstance();
			SlashCommand serverCommand = commandObj.getBuilder(instance).createForServer(server).join();
			serverCommands.computeIfAbsent(instanceID, k -> new HashMap<>()).put(serverCommand.getId(), commandType);
			return serverCommand.getId();
		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			LOGGER.trace(e.getStackTrace());
			return -1;
		}
	}

	public static void executeCommand(SlashCommandInteraction interaction) {
		String instanceID = InstanceManager.getInstanceByServerID(interaction.getServer().get().getId()).get().getInstanceID();
		long commandID = interaction.getCommandId();
		if (serverCommands.get(instanceID).containsKey(commandID)) {
			try {
				serverCommands.get(instanceID).get(commandID)
						.getConstructor().newInstance()
						.execute(interaction);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			// Unregistered command!
			throw new CanvaCordException("Unregistered command found");
		}
	}

}
