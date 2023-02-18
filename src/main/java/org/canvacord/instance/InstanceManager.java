package org.canvacord.instance;

import org.canvacord.setup.InstanceCreateWizard;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class InstanceManager {

	private static Map<String, Instance> instances;
	private static List<Instance> runningInstances;
	private static Set<String> runningInstanceIDs;

	static {
		instances = new HashMap<>();
		runningInstances = new ArrayList<>();
		runningInstanceIDs = new HashSet<>();
	}

	public static void loadInstances() {

		// TODO: load saved instances from disk

	}

	public static List<Instance> getInstances() {
		return instances.values().stream().toList();
	}

	public static boolean runInstance(String instanceID) {

		// check already running
		if (runningInstanceIDs.contains(instanceID) || !instances.containsKey(instanceID))
			return false;

		Instance instance = instances.get(instanceID);
		instance.start();

		return true;
	}

	public static boolean stopInstance(String instanceID) {

		// check already running
		if (!runningInstanceIDs.contains(instanceID) || !instances.containsKey(instanceID))
			return false;

		Instance instance = instances.get(instanceID);
		instance.stop();

		return true;
	}

	public static Optional<String> generateNewInstance() {

		// Create and run a wizard to get the user to set up the instance
		InstanceCreateWizard wizard = new InstanceCreateWizard();
		wizard.runWizard();

		// If the wizard process did not complete successfully return empty
		if (!wizard.completedSuccessfully())
			return Optional.empty();

		// Otherwise, get the resulting configuration and generate an instance with it
		InstanceConfiguration configuration = wizard.getResult();

		AtomicReference<String> instanceID = new AtomicReference<>();

		createInstance(configuration).ifPresentOrElse(
				instance -> {

					// store the instance in the map
					instances.put(instance.getInstanceID(), instance);

					// TODO:
					// if instantiation is successful, save the config to disk
					InstanceWriter.writeInstance(instance);

					// Additionally, create its data file
					InstanceDataManager.createInstanceData(instance.getInstanceID());

					// return the instance's ID so the caller can decide when to initialize it
					instanceID.set(instance.getInstanceID());
				},
				() -> {
					// TODO handle instance creation exception
					instanceID.set("");
				}
		);

		return Optional.of(instanceID.get());

	}

	private static Optional<Instance> createInstance(InstanceConfiguration configuration) {
		String courseID = configuration.getCourseID();
		long serverID = configuration.getServerID();
		try {
			return Optional.of(new Instance(courseID, serverID, configuration));
		}
		catch (InstantiationException e) {
			return Optional.empty();
		}
	}

}
