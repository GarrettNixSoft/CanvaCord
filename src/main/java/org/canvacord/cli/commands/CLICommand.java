package org.canvacord.cli.commands;

import org.canvacord.util.data.Stack;

import java.util.HashMap;
import java.util.Map;

public abstract class CLICommand {

	private final String name;

	public CLICommand(String name) {
		this.name = name;
	}

	protected Map<String, Class<? extends CLICommand>> subCommands = new HashMap<>();

	public abstract boolean execute(Stack<String> args);
	public abstract String getUsage();

	protected void printUsage() {
		System.out.println(getUsage());
	}

}
