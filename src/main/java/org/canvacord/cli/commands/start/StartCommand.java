package org.canvacord.cli.commands.start;

import org.canvacord.cli.commands.CLIHubCommand;

public class StartCommand extends CLIHubCommand {

	public StartCommand() {
		super("start");
		subCommands.put("all", StartAllCommand.class);
		subCommands.put("-a", StartAllCommand.class);
		subCommands.put("id", StartInstanceCommand.class);
		subCommands.put("-i", StartInstanceCommand.class);
	}

	@Override
	public String getUsage() {
		return 	"""
				usage: start [options]
				options:
					all, -a					Start all instances
					id, -i <instance_id>		Start an instance by its ID
				""";
	}
}
