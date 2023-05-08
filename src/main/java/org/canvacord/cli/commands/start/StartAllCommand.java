package org.canvacord.cli.commands.start;

import org.canvacord.cli.commands.CLICommand;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.data.Stack;

public class StartAllCommand extends CLICommand {

	public StartAllCommand() {
		super("start all");
	}

	@Override
	public boolean execute(Stack<String> args) {

		try {
			InstanceManager.runAllInstances();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return false;

	}

	@Override
	public String getUsage() {
		return "";
	}
}
