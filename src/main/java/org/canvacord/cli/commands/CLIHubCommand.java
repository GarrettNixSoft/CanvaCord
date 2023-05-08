package org.canvacord.cli.commands;

import org.canvacord.util.data.Stack;

public abstract class CLIHubCommand extends CLICommand {

	public CLIHubCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(Stack<String> args) {

		// if they did not supply a required argument
		if (args.isEmpty()) {
			printUsage();
			return false;
		}

		// get the first argument string
		String arg = args.poll();

		// if it's a valid subcommand
		if (subCommands.containsKey(arg)) {
			try {
				subCommands.get(arg).getConstructor().newInstance().execute(args);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		// otherwise show how to use this command
		else {
			printUsage();
		}

		// this command does not terminate the program
		return false;
	}



}
