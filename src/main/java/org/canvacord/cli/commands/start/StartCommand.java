package org.canvacord.cli.commands.start;

import org.canvacord.cli.commands.CLICommand;
import org.canvacord.util.data.Stack;

public class StartCommand extends CLICommand {

	public StartCommand() {
		super("start");
		subCommands.put("-a", StartAllCommand.class);
		subCommands.put("-all", StartAllCommand.class);
		subCommands.put("-i", StartInstanceCommand.class);
		subCommands.put("-id", StartInstanceCommand.class);
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
				usage: start [options]
				options:
					-a, -all					Start all instances
					-i, -id <instance_id>		Start an instance by its ID
				""";
	}
}
