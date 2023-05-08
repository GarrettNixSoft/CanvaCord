package org.canvacord.cli.commands;

import org.canvacord.util.data.Stack;

public class QuitCommand extends CLICommand {

	@Override
	public boolean execute(Stack<String> args) {
		return true;
	}

	@Override
	public String getUsage() {
		return "usage: [quit|exit]";
	}
}
