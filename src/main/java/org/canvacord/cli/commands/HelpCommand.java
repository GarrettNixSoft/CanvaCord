package org.canvacord.cli.commands;

import org.canvacord.util.data.Stack;

public class HelpCommand extends CLICommand {

	public HelpCommand() {
		super("help");
	}

	@Override
	public boolean execute(Stack<String> args) {

		if (args.isEmpty()) {
			// TODO
		}
		else {
			// TODO
		}

		return false;
	}

	@Override
	public String getUsage() {
		return null;
	}
}
