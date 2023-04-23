package org.canvacord.discord.commands;

import org.javacord.api.entity.server.Server;
import java.util.Set;

import static org.canvacord.discord.commands.CommandHandler.registerCommandServer;

public class CommandBuilder {

	/**
	 * Takes the requested slash commands and registers them in the provided server
	 *
	 * @param server The server that commands are being built for.
	 * @param serverActiveCommands A set of owner chosen commands that determines what's available.
	 * */
	public static void buildCommandsForServer(Server server, Set<Class<? extends Command>> serverActiveCommands) {
		for (Class<? extends Command> command : serverActiveCommands){
			registerCommandServer(command, server);
		}
		// Help command is registered last, so it can populate its autocomplete choices
		registerCommandServer(HelpCommand.class, server);
	}
}
