package org.canvacord.instance;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.util.file.FileUtil;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public class InstanceLoader {

	/**
	 * Load a saved CanvaCord instance configuration from disk.
	 * @param instanceID the ID of the instance to load
	 * @return an Instance object constructed from the data saved to disk
	 * @throws CanvaCordException when the instances directory is not found and cannot be created for some reason
	 */
	public static Optional<Instance> loadInstance(String instanceID) throws CanvaCordException {

		// Check for "instances" folder in the working directory
		File instanceDir = Paths.get("instances/" + instanceID).toFile();

		// If it's missing, attempt to create it
		if (!instanceDir.exists()) {
			boolean success = instanceDir.mkdirs();
			if (!success)
				throw new CanvaCordException("Could not create instances directory!");
			// Directory created successfully; return empty since the instance file can't exist
			else
				return Optional.empty();
		}
		// Otherwise if it exists, but is not a directory, throw an error
		else if (!instanceDir.isDirectory())
			throw new CanvaCordException("instances is a file, not a directory!");

		// Otherwise, check for the target file
		File instanceFile = Paths.get("instances/" + instanceID + "/config.json").toFile();

		// If the file does not exist, return empty
		if (!instanceFile.exists())
			return Optional.empty();
		// Otherwise, load the instance and return it
		else {
			Optional<JSONObject> instanceJSON = FileUtil.getJSON(instanceFile);
			if (instanceJSON.isPresent())
				return Optional.of(InstanceParser.parseInstance(instanceID, instanceJSON.get()));
			else
				throw new CanvaCordException("Failed to load instance file! " + instanceFile.getPath());
		}

	}

	/**
	 * Check whether a given file is a potentially valid CanvaCord instance file.
	 * At the moment, this just means its file extension indicates it's a JSON file.
	 * @param file the file to check
	 * @return {@code true} if the file exists and is potentially a valid CanvaCord instance file
	 */
	private static boolean isValidInstanceFile(File file) {
		return file.exists() && FileUtil.getFileExtension(file).equalsIgnoreCase("json");
	}

}
