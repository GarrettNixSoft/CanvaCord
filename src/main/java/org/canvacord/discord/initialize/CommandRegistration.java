package org.canvacord.discord.initialize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.discord.DiscordBot;
import org.canvacord.discord.commands.Command;
import org.canvacord.discord.commands.CommandHandler;
import org.canvacord.instance.Instance;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.json.JSONObject;

public class CommandRegistration {

	private static final Logger LOGGER = LogManager.getLogger(CommandRegistration.class);

	public static void registerCommands(Instance instance) {

		LOGGER.info("Registering commands for instance " + instance.getName());

		// Grab the map of commands we need to enable and the map of their IDs
		JSONObject commandAvailability = instance.getCommandAvailability();
		JSONObject commandIDs = instance.getCommandIDs();

		// Get the Discord API reference and use it to fetch the target server
		DiscordApi api = DiscordBot.getBotInstance().getApi();
		Server server = api.getServerById(instance.getServerID()).orElseThrow();

		// Iterate over the command keys and register the commands
		for (String commandKey : commandAvailability.keySet()) {
			// Only process enabled commands
			if (commandAvailability.getBoolean(commandKey)) {
				// Log the attempt
				LOGGER.debug("Attempting to register command " + commandKey);
				// Get the command class
				Class<? extends Command> commandClass = Command.COMMANDS_BY_NAME.get(commandKey);
				// Check for commands that aren't implemented yet and skip them
				if (commandClass == null) {
					LOGGER.warn("Command " + commandKey + " has no class available, skipping it for now");
					continue;
				}
				// Register the command
				long commandID = CommandHandler.registerCommandServer(commandClass, server);
				// Store the ID
				commandIDs.put(commandKey, commandID);
			}
		}

		// Register the help command
		long helpCommandID = CommandHandler.registerCommandServer(Command.COMMANDS_BY_NAME.get("help"), server);
		commandIDs.put("help", helpCommandID);

		// Save the IDs to the Instance configuration
		instance.getConfiguration().setCommandIDs(commandIDs);

	}

}
