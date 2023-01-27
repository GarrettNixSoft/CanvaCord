package org.canvacord.instance;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.util.file.FileUtil;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;

public class InstanceLoader {

	/**
	 * Load a saved CanvaCord instance configuration from disk.
	 * @param instanceID the ID of the instance to load
	 * @return an Instance object constructed from the data saved to disk
	 * @throws CanvaCordException when the instances directory is not found and cannot be created for some reason
	 */
	public static Instance loadInstance(String instanceID) throws CanvaCordException {

		// Check for "instances" folder in the working directory
		File instanceDir = Paths.get("instances").toFile();

		// If it's missing, attempt to create it
		if (!instanceDir.exists()) {

			boolean success = instanceDir.mkdir();

			if (!success)
				throw new CanvaCordException("Could not create instances directory!");

			return null;

		}
		// Otherwise if it exists, but is not a directory, throw an error
		else if (!instanceDir.isDirectory())
			throw new CanvaCordException("instances is a file, not a directory!");

		// Otherwise, check for the target file
		File[] instanceFiles = instanceDir.listFiles();

		for (File file : instanceFiles) {

			// Check if the file name pattern indicates a valid instance file
			if (!isValidInstanceFile(file)) {
				continue;
			}

			// If it could be an instance file, check if the name matches the ID exactly
			if (FileUtil.getFileName(file).equals(instanceID)) {

				// If it's a match, send it to the parser and return the result
				JSONObject instanceJSON = FileUtil.getJSON(file);
				return InstanceParser.parseInstance(instanceID, instanceJSON);

			}

		}

		// if no matches were found, return null without throwing an exception to indicate the instance file was not found
		return null;

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
