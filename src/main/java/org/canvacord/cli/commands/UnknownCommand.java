package org.canvacord.cli.commands;

import org.canvacord.util.data.Stack;

public class UnknownCommand extends CLICommand {
	@Override
	public boolean execute(Stack<String> args) {
		System.out.println("Unknown command. Type help to list commands.");
		return false;
	}

	@Override
	public String getUsage() {
		return "";
	}
}
