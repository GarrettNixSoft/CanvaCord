package org.canvacord.cli.commands.stop;

import org.canvacord.cli.commands.CLIHubCommand;

public class StopCommand extends CLIHubCommand {

	public StopCommand() {
		super("stop");
		subCommands.put("-a", StopAllCommand.class);
		subCommands.put("-all", StopAllCommand.class);
		subCommands.put("-i", StopInstanceCommand.class);
		subCommands.put("-id", StopInstanceCommand.class);
	}

	@Override
	public String getUsage() {
		return 	"""
				usage: stop [options]
				options:
					-a, -all					Stop all instances
					-i, -id <instance_id>		Stop an instance by its ID
				""";
	}
}
