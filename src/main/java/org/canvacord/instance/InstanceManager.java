package org.canvacord.instance;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.setup.InstanceCreateWizard;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.quartz.SchedulerException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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

		try {

			// Find all files in the instances directory
			File instancesDir = Paths.get("instances/").toFile();
			File[] potentialInstances = instancesDir.listFiles();

			// Iterate over the files and attempt to read them as instances
			for (File file : potentialInstances) {

				try {
					// Instances are contained in subdirectories; if this is not a directory, skip it
					if (!file.isDirectory())
						continue;

					// List the instance files
					File[] instanceFiles = file.listFiles();

					// Instances contain 2 files: config and data
					if (instanceFiles.length != 2)
						continue;

					// Both files must be JSON files
					if (!FileUtil.getFileExtension(instanceFiles[0]).equals("json") ||
						!FileUtil.getFileExtension(instanceFiles[1]).equals("json"))
						continue;

					// The two JSON files must be named config.json and data.json
					if (Instance.isValidInstanceData(instanceFiles)) {
						// Get the instance ID from the directory name
						String instanceID = FileUtil.getFileName(file);
						// Load the instance
						InstanceLoader.loadInstance(instanceID).ifPresent(
								instance -> {
									instances.put(instanceID, instance);
								}
						);
					}
					else {
						throw new NullPointerException();
					}

				}
				catch (NullPointerException e) {
					UserInput.showErrorMessage("There is bad data in the instances folder.", "Bad Instance Data");
				}
			}
		}
		catch (Exception e) {
			UserInput.showExceptionWarning(e);
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public static List<Instance> getInstances() {
		return instances.values().stream().toList();
	}

	public static boolean runInstance(String instanceID) throws SchedulerException {

		// check already running
		if (runningInstanceIDs.contains(instanceID) || !instances.containsKey(instanceID))
			return false;

		Instance instance = instances.get(instanceID);
		instance.start();

		return true;
	}

	public static boolean stopInstance(String instanceID) throws SchedulerException {

		// check already running
		if (!runningInstanceIDs.contains(instanceID) || !instances.containsKey(instanceID))
			return false;

		Instance instance = instances.get(instanceID);
		instance.stop();

		return true;
	}

	public static void stopAllInstances() throws SchedulerException {
		for (String runningInstanceID : runningInstanceIDs) {
			if (!stopInstance(runningInstanceID))
				throw new CanvaCordException("Failed to shut down instance " + instances.get(runningInstanceID).getName());
		}
	}

	public static Optional<Instance> generateNewInstance() {

		// Create and run a wizard to get the user to set up the instance
		InstanceCreateWizard wizard = new InstanceCreateWizard();
		wizard.runWizard();

		// If the wizard process did not complete successfully return empty
		if (!wizard.completedSuccessfully())
			return Optional.empty();

		// Otherwise, get the resulting configuration and generate an instance with it
		InstanceConfiguration configuration = wizard.getResult();

		AtomicReference<Instance> instanceRef = new AtomicReference<>();

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
					instanceRef.set(instance);
				},
				() -> {
					// TODO handle instance creation exception
				}
		);

		return Optional.of(instanceRef.get());

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
