package org.canvacord.util.file;

import org.canvacord.util.input.UserInput;
import org.canvacord.util.string.StringConverter;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The FileUtil class contains various utility methods for working with and
 * loading data from files. Most of these methods are copied from projects
 * I've worked on in the past, but it's still code I wrote.
 * -Garrett
 */
public class FileUtil {

	public static final String SEPARATOR;

	static {
		SEPARATOR = System.getProperty("file.separator");
//		SEPARATOR = "/";
	}

	public static Optional<List<String>> getFileData(File file) {
		List<String> data = new ArrayList<>();
		try {
			InputStream in = FileUtil.class.getResourceAsStream(file.getPath());
			if (in == null)
				in = ResourceLoader.getResourceAsStream(file.getPath());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				data.add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
		return Optional.of(data);
	}

	public static Optional<JSONObject> getJSON(File file) {
		Optional<List<String>> fileData = getFileData(file);
		if (fileData.isEmpty())
			return Optional.empty();
		else {
			String combined = StringConverter.combineAll(fileData.get());
			return Optional.of(new JSONObject(combined));
		}

	}

	public static Optional<JSONArray> getJSONFileAsJSONArray(File file) {
		Optional<List<String>> fileData = getFileData(file);
		if (fileData.isEmpty())
			return Optional.empty();
		else {
			String combinedData = StringConverter.combineAll(fileData.get());
			return Optional.of(new JSONArray(combinedData));
		}
	}

	/**
	 * Get a file's name without the extension. If the
	 * specified file has no extension, the result is
	 * the equivalent of calling {@code File.getName()}.
	 * @param file the file to get the name of
	 * @return a {@code String} containing the file name with the extension removed
	 */
	public static String getFileName(File file) {
		String fileName = file.getName();
		int extensionIndex = fileName.lastIndexOf('.');
		if (extensionIndex == -1)
			return fileName;
		else
			return fileName.substring(0, extensionIndex);
	}

	/**
	 * Get the extension of a file, if it has one. Does not
	 * include the period.
	 * @param file the file to get the extension of
	 * @return a {@code String} containing all characters following the last period in the file name
	 */
	public static String getFileExtension(File file) {
		String fileName = file.getName();
		int extensionIndex = fileName.lastIndexOf('.');
		if (extensionIndex == -1) return "";
		else return fileName.substring(extensionIndex + 1);
	}

	/**
	 * Write the data in the given {@code JSONObject} into a file
	 * at the path specified by the {@code file} String.
	 * <br>
	 * If the file exists, it will be overwritten.
	 * <br>
	 * If the file does not exist, and/or one or more directories
	 * in the file's specified path do not exist, they will be
	 * created before attempting to write to the file.
	 * @param json the {@code JSONObject} to write to disk
	 * @param file the path for the desired file
	 */
	public static boolean writeJSON(JSONObject json, File file) {
		// wrap in a generic try/catch for any unexpected errors
		try {
			// if the file doesn't exist,
			if (!file.exists()) {
				// attempt to create it;
				try {
					boolean success = file.createNewFile();
					if (success) {
						// if the creation succeeds, write the JSON to it
						writeJSONToFile(json, file);
						return true;
					}
					else throw new IOException("move to catch block");
				}
				// if creation failed,
				catch (IOException e) {
					// it may be because the path specifies some number of parent directories that don't exist;
					int sepIndex = file.getPath().lastIndexOf(SEPARATOR);
					String directoryPath = file.getPath().substring(0, sepIndex);
					File directory = new File(directoryPath);
					// try creating those parent directories,
					if (directory.mkdirs()) {
						// then try creating the file again;
						if (file.createNewFile()) {
							// if succeeded, write to it
							writeJSONToFile(json, file);
							return true;
						} else throw new RuntimeException("File " + file + " did not exist and could not be created");
					}
					else throw new RuntimeException("File " + file + " did not exist and could not be created");
				}
			}
			// otherwise, if the file does exist, overwrite it
			else {
				writeJSONToFile(json, file);
				return true;
			}
		} catch (Exception e) {
			// generic fail state
			e.printStackTrace();
			return false;
		}
	}

	private static void writeJSONToFile(JSONObject json, File file) {
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fw);
			String jsonStr = json.toString(4);
//					Logger.log("JSON to write to file: " + jsonStr);
			String[] data = jsonStr.split("\n");
			for (String line : data) {
				writer.write(line);
				writer.write(System.lineSeparator());
			}
			// and done
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isValidFile(File file, String... extensions) {

		if (!file.exists())
			return false;

		for (String ext : extensions)
			if (getFileExtension(file).equalsIgnoreCase(ext))
				return true;

		return false;

	}

	/**
	 * Validate a file.
	 * @param file the file to validate
	 * @param extensions all extensions that should be considered valid
	 * @return {@code true} if the file exists and its extension matches one of the given extensions
	 */
	public static boolean isValidFile(String file, String... extensions) {
		return isValidFile(new File(file), extensions);
	}

	/**
	 * Delete a directory and all of its files and subdirectories.
	 * @param dir the directory to delete
	 */
	public static void deleteDirectory(File dir) {

		try {

			File[] files = dir.listFiles();

			for (File file : files) {

				if (file.isDirectory())
					deleteDirectory(file);
				else
					Files.delete(file.toPath());

			}

			Files.delete(dir.toPath());

		}
		catch (Exception e) {
			UserInput.showExceptionWarning(e);
			e.printStackTrace();
		}

	}

}
