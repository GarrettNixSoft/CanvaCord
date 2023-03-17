package org.canvacord.util.file;

import com.google.common.hash.Hashing;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FileHasher {

	private static Map<String, String> hashRecords = new HashMap<>();

	public static String hashFile(File file) {
		if (hashRecords.containsKey(file.getPath()))
			return hashRecords.get(file.getPath());
		else {
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
			return result.get();
		}
	}

}
