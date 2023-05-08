package org.canvacord.cli.commands.instance;

import org.canvacord.cli.commands.CLIHubCommand;

public class InstanceCommand extends CLIHubCommand {

	public InstanceCommand() {
		super("instance");
		subCommands.put("create", InstanceCreateCommand.class);
		subCommands.put("-c", InstanceCreateCommand.class);
		subCommands.put("edit", InstanceEditCommand.class);
		subCommands.put("-e", InstanceEditCommand.class);
		subCommands.put("delete", InstanceDeleteCommand.class);
		subCommands.put("-d", InstanceDeleteCommand.class);
	}

	@Override
	public String getUsage() {
		return """
				usage: instance [create|edit|delete]
				options:
					create, -c			Create a new instance
					edit, -e			Edit an existing instance
					delete, -d			Delete an instance
				""";
	}
}
