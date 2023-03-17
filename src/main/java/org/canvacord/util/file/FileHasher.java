package org.canvacord.util.file;

import com.google.common.hash.Hashing;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FileHasher {

	private static final Map<String, String> hashRecords = new HashMap<>();

	@SuppressWarnings("UnstableApiUsage")
	public static String hashFile(File file) {
		// If this file has been hashed before, return the previous result
		if (hashRecords.containsKey(file.getPath()))
			return hashRecords.get(file.getPath());
		else {
			// Hash the file
			AtomicReference<String> result = new AtomicReference<>();
			FileUtil.getFileBytes(file).ifPresentOrElse(
					data -> {
						String sha256 = Hashing.sha256().hashBytes(data).toString();
						result.set(sha256);
					},
					() -> {
						result.set("null");
					}
			);
			// Store the result for future reference
			hashRecords.put(file.getPath(), result.get());
			// Return the result
			return result.get();
		}
	}

}
