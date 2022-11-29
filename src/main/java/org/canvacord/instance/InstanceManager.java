package org.canvacord.instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstanceManager {

	private static List<Instance> instances;
	private static List<Instance> runningInstances;
	private static Set<String> runningInstanceIDs;

	public static void loadInstances() {

		instances = new ArrayList<>();
		runningInstances = new ArrayList<>();
		runningInstanceIDs = new HashSet<>();

		// TODO: load saved instances from disk

	}

	public static boolean runInstance(String instanceID) {
		// TODO
		return false;
	}

	public static boolean stopInstance(String instanceID) {
		// TODO
		return false;
	}

	public static String generateNewInstance(InstanceConfiguration configuration) {

		Instance instance = createInstance(configuration);
		instances.add(instance);

		// TODO:
		// if instantiation is successful, save the config to disk

		// return the instance's ID so the caller can decide when to initialize it
		return instance.getInstanceID();

	}

	private static Instance createInstance(InstanceConfiguration configuration) {
		// TODO
		return null;
	}

}
