package org.canvacord.cli.commands;

import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.data.Stack;

import java.util.List;

public class ListCommand extends CLICommand {

	public ListCommand() {
		super("list");
	}

	@Override
	public boolean execute(Stack<String> args) {

		List<Instance> instances = InstanceManager.getInstances();

		System.out.println("\nInstances:");
		for (int i = 0; i < instances.size(); i++) {
			Instance instance = instances.get(i);
			System.out.println("\t" + (i + 1) + ". " + instance.getName() + " (" + instance.getInstanceID() + ") -- Running: " + (InstanceManager.isInstanceRunning(instance.getInstanceID()) ? "Yes" : "No"));
		}
		System.out.println();

		return false;

	}

	@Override
	public String getUsage() {
		return null;
	}
}
