package org.canvacord.instance;

import org.canvacord.event.CanvaCordEvent;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.persist.CacheManager;
import org.canvacord.setup.InstanceCreateWizard;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.quartz.SchedulerException;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class InstanceManager {

	private static final Map<String, Instance> instances;
	private static final List<Instance> runningInstances;
	private static final Set<String> runningInstanceIDs;

	private static final Map<String, Instance> instancesByCourseID;
	private static final Map<Long, Instance> instancesByServerID;

	static {
		instances = new HashMap<>();
		runningInstances = new ArrayList<>();
		runningInstanceIDs = new HashSet<>();

		instancesByCourseID = new HashMap<>();
		instancesByServerID = new HashMap<>();
	}

	private static void addInstance(Instance instance) {
		instances.put(instance.getInstanceID(), instance);
		instancesByCourseID.put(instance.getCourseID(), instance);
		instancesByServerID.put(instance.getServerID(), instance);
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
								InstanceManager::addInstance
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

	public static Optional<Instance> getInstanceByCourseID(String courseID) {
		return Optional.ofNullable(instancesByCourseID.get(courseID));
	}

	public static Optional<Instance> getInstanceByServerID(long serverID) {
		return Optional.ofNullable(instancesByServerID.get(serverID));
	}

	public static boolean runInstance(String instanceID) throws SchedulerException {

		// check already running
		if (runningInstanceIDs.contains(instanceID) || !instances.containsKey(instanceID))
			return false;

		Instance instance = instances.get(instanceID);
		instance.start();

		runningInstances.add(instance);
		runningInstanceIDs.add(instanceID);

		CanvaCordEvent.newEvent(CanvaCordEvent.Type.INSTANCE_STARTED, instance);

		return true;
	}

	public static boolean stopInstance(String instanceID) throws SchedulerException {

		// check already running
		if (!runningInstanceIDs.contains(instanceID) || !instances.containsKey(instanceID))
			return false;

		Instance instance = instances.get(instanceID);
		instance.stop();

		runningInstances.remove(instance);
		runningInstanceIDs.remove(instanceID);

		CanvaCordEvent.newEvent(CanvaCordEvent.Type.INSTANCE_STOPPED, instance);

		return true;
	}

	public static void runAllInstances() throws SchedulerException {
		for (String instanceID : instances.keySet()) {
			if (!runningInstanceIDs.contains(instanceID))
				if (!runInstance(instanceID))
					throw new CanvaCordException("Failed to start instance " + instances.get(instanceID).getName());
		}
	}

	public static void stopAllInstances() throws SchedulerException {
		System.out.println("Stop all");
		for (String runningInstanceID : runningInstanceIDs) {
			if (!stopInstance(runningInstanceID))
				throw new CanvaCordException("Failed to shut down instance " + instances.get(runningInstanceID).getName());
		}
	}

	public static void updateInstance(String instanceID) {

		try {
			instances.get(instanceID).update();
		}
		catch (Exception e) {
			e.printStackTrace();
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
					addInstance(instance);

					// TODO:
					// if instantiation is successful, save the config to disk
					InstanceWriter.writeInstance(instance);

					// Additionally, create its data file
					CacheManager.createInstanceData(instance);

					// return the instance's ID so the caller can decide when to initialize it
					instanceRef.set(instance);
				},
				() -> {
					// TODO handle instance creation exception
					System.out.println("Instance creation failed");
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
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public static void deleteInstance(Instance instance) {

		try {
			// Stop the instance
			stopInstance(instance.getInstanceID());

			// Find the instance folder
			File instanceDir = Paths.get("instances/" + instance.getInstanceID()).toFile();
			FileUtil.deleteDirectory(instanceDir);

			instances.remove(instance.getInstanceID());
			Instance.acknowledgeDeleted(instance);

			// Send an event signalling the deletion occurred
			CanvaCordEvent.newEvent(CanvaCordEvent.Type.INSTANCE_DELETED, instance);
		}
		catch (Exception e) {
			UserInput.showExceptionWarning(e);
			e.printStackTrace();
		}

	}

}
