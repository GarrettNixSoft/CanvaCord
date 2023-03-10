package org.canvacord.util.net;

import org.canvacord.util.input.UserInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

public class RemoteFileGetter {

	public static InputStream getRemoteFileStream(String url) {
		return getRemoteFileStream(url, true);
	}

	public static InputStream getRemoteFileStream(String url, boolean showErrorsToUser) {

		try {
			return new URL(url).openStream();
		}
		catch (MalformedURLException urlException) {
			if (showErrorsToUser) {
				String message = String.format("Please check the URL provided.\n(%s)", url);
				UserInput.showErrorMessage(message, "Malformed URL Exception");
			}
			urlException.printStackTrace();
			return null;
		}
		catch (IOException ioException) {
			if (showErrorsToUser) {
				String message = String.format("An IO error occurred fetching the file.\n(%s)", url);
				UserInput.showErrorMessage(message, "");
			}
			return null;
		}

	}

	public static Optional<File> downloadAndSave(String url, Path saveTarget) {

		try (FileOutputStream outputStream = new FileOutputStream(saveTarget.toFile())) {
			InputStream downloadStream = getRemoteFileStream(url, false);
			if (downloadStream == null)
				return Optional.empty();
			outputStream.write(downloadStream.readAllBytes());
			return Optional.of(saveTarget.toFile());
		}
		catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}

	}

}
