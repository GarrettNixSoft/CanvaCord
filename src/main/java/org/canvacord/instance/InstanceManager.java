package org.canvacord.instance;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class InstanceManager {

	private static Map<String, Instance> instances;
	private static List<Instance> runningInstances;
	private static Set<String> runningInstanceIDs;

	public static void loadInstances() {

		instances = new HashMap<>();
		runningInstances = new ArrayList<>();
		runningInstanceIDs = new HashSet<>();

		// TODO: load saved instances from disk

	}

	public static boolean runInstance(String instanceID) {

		// check already running
		if (runningInstanceIDs.contains(instanceID))
			return false;

		Instance instance = instances.get(instanceID);
		instance.start();

		return false;
	}

	public static boolean stopInstance(String instanceID) {
		// TODO
		return false;
	}

	public static String generateNewInstance(InstanceConfiguration configuration) {

		AtomicReference<String> instanceID = new AtomicReference<>();

		createInstance(configuration).ifPresentOrElse(
				instance -> {
					instances.put(instance.getInstanceID(), instance);

					// TODO:
					// if instantiation is successful, save the config to disk

					// return the instance's ID so the caller can decide when to initialize it
					instanceID.set(instance.getInstanceID());
				},
				() -> {
					// TODO handle instance creation exception
					instanceID.set("");
				}
		);

		return instanceID.get();


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
