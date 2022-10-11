package org.canvacord.instance;

import java.util.List;

public class InstanceManager {

	private static List<Instance> instances;

	public static void generateNewInstance(InstanceConfiguration configuration) {

		Instance instance = createInstance(configuration);
		instances.add(instance);

		// TODO:
		// whether to initialize it or not
		// if instantiation is successful, save the config to disk

	}

	private static Instance createInstance(InstanceConfiguration configuration) {
		// TODO
		return null;
	}

}
