package org.canvacord.cli.commands.start;

import org.canvacord.cli.commands.CLICommand;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.data.Stack;

public class StartInstanceCommand extends CLICommand {

	public StartInstanceCommand() {
		super("start instance");
	}

	@Override
	public boolean execute(Stack<String> args) {

		if (args.isEmpty()) {
			printUsage();
		}
		else {
			String instanceID = args.poll();
			try {
				boolean success = InstanceManager.runInstance(instanceID);
				if (!success) System.out.println("No instance with id " + instanceID + " was found");
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		return false;
	}

	@Override
	public String getUsage() {
		return "usage: start [-i | -id] <instance_id> 		Start an instance by its ID";
	}
}
