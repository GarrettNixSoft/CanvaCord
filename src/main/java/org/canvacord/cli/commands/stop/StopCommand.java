package org.canvacord.cli.commands.stop;

import org.canvacord.cli.commands.CLICommand;
import org.canvacord.util.data.Stack;

public class StopCommand extends CLICommand {

	public StopCommand() {
		super("stop");
		subCommands.put("-a", StopAllCommand.class);
		subCommands.put("-all", StopAllCommand.class);
		subCommands.put("-i", StopInstanceCommand.class);
		subCommands.put("-id", StopInstanceCommand.class);
	}

	@Override
	public boolean execute(Stack<String> args) {

		// if they did not supply a required argument
		if (args.isEmpty()) {
			System.out.println(getUsage());
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
