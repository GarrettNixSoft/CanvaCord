package org.canvacord.instance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.persist.CacheManager;
import org.canvacord.util.file.FileUtil;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public class InstanceLoader {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Load a saved CanvaCord instance configuration from disk.
	 * @param instanceID the ID of the instance to load
	 * @return an Instance object constructed from the data saved to disk
	 * @throws CanvaCordException when the instances directory is not found and cannot be created for some reason
	 */
	protected static Optional<Instance> loadInstance(String instanceID) throws CanvaCordException {

		// Check for "instances" folder in the working directory
		File allInstancesDir = Paths.get("instances/").toFile();

		// If it's missing, attempt to create it
		if (!allInstancesDir.exists()) {
			boolean success = allInstancesDir.mkdirs();
			if (!success)
				throw new CanvaCordException("Could not create instances directory!");
			// Directory created successfully; return empty since the instance file can't exist
			else
				return Optional.empty();
		}
		// Otherwise if it exists, but is not a directory, throw an error
		else if (!allInstancesDir.isDirectory())
			throw new CanvaCordException("/instances is a file, not a directory!");

		// Otherwise, check for the target file
		File instanceFile = Paths.get("instances/" + instanceID + "/config.json").toFile();

		// If the file does not exist, return empty
		if (!instanceFile.exists())
			return Optional.empty();
		// Otherwise, load the instance and return it
		else {
			Optional<JSONObject> instanceJSON = FileUtil.getJSON(instanceFile);
			if (instanceJSON.isPresent()) {
				Instance parsedInstance = InstanceParser.parseInstance(instanceID, instanceJSON.get());
				CacheManager.loadInstanceData(parsedInstance);
				LOGGER.debug("Loaded instance " + instanceID);
				return Optional.of(parsedInstance);
			}
			else
				throw new CanvaCordException("Failed to load instance file! " + instanceFile.getPath());
		}

	}

}
