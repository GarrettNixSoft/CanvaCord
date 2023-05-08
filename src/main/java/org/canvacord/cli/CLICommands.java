package org.canvacord.cli;

import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;

import java.util.List;

public class CLICommands {

	public static CLICommandHandler listCommand = args -> {

		List<Instance> instances = InstanceManager.getInstances();

		System.out.println("\nInstances:");
		for (int i = 0; i < instances.size(); i++) {
			Instance instance = instances.get(i);
			System.out.println("\t" + (i + 1) + ". " + instance.getName() + " (" + instance.getInstanceID() + ")");
		}
		System.out.println();

		return false;

	};

	public static CLICommandHandler startCommand = args -> {

		// if they did not supply a required argument
		if (args.length == 1) {
			System.out.println(
      					"""
						usage: start [options]
						options:
							-a, -all					Start all instances
							-i, -id <instance_id>		Start an instance by its ID
						""");
		}
		// if they supplied the all argument
		else if (args[1].equals("-a") || args[1].equals("-all")) {
			try {
				InstanceManager.runAllInstances();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		// if they supplied the instance argument
		else if (args[1].equals("-i")) {
			if (args.length < 3) {
				System.out.println(
						"""
						usage: start [-i | -id] <instance_id> 		Start an instance by its ID
						""");
			}
			else {
				String instanceID = args[2];
				try {
					boolean success = InstanceManager.runInstance(instanceID);
					if (!success) System.out.println("No instance with id " + instanceID + " was found");
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		else {
			System.out.println(
					"""
					usage: start [options]
					options:
						-a, -all					Start all instances
						-i, -id <instance_id>		Start an instance by its ID
					""");
		}

		return false;

	};

}
