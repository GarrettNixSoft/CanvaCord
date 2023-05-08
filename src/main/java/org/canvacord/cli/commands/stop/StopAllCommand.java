package org.canvacord.cli.commands.stop;

import org.canvacord.cli.commands.CLICommand;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.data.Stack;

public class StopAllCommand extends CLICommand {

	public StopAllCommand() {
		super("stop all");
	}

	@Override
	public boolean execute(Stack<String> args) {

		// try to stop all instances
		try {
			InstanceManager.stopAllInstances();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// this command does not terminate the program
		return false;

	}

	@Override
	public String getUsage() {
		return "";
	}
}
