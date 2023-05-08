package org.canvacord.cli.commands.instance;

import org.canvacord.cli.commands.CLICommand;
import org.canvacord.util.data.Stack;

public class InstanceEditCommand extends CLICommand {

	public InstanceEditCommand() {
		super("instance edit");
	}

	@Override
	public boolean execute(Stack<String> args) {
		return false;
	}

	@Override
	public String getUsage() {
		return null;
	}
}
