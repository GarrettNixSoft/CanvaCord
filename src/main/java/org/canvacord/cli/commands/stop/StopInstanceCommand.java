package org.canvacord.cli.commands.stop;

import org.canvacord.cli.commands.CLICommand;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.data.Stack;

public class StopInstanceCommand extends CLICommand {

	public StopInstanceCommand() {
		super("stop instance");
	}

	@Override
	public boolean execute(Stack<String> args) {

		// if the user does not supply an ID argument
		if (args.isEmpty()) {
			printUsage();
		}

		// try to stop an instance with the given ID
		else {
			String instanceID = args.poll();
			try {
				boolean success = InstanceManager.stopInstance(instanceID);
				if (!success) System.out.println("No instance with id " + instanceID + " was found");
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		// this command does not terminate the program
		return false;
	}

	@Override
	public String getUsage() {
		return "usage: stop [-i | -id] <instance_id> 		Stop an instance by its ID";
	}
}
