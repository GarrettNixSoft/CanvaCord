package org.canvacord.util.file;

import org.canvacord.util.string.StringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileUtil {

	public static ArrayList<String> getFileData(File file) {
		ArrayList<String> data = new ArrayList<>();
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
			return data;
		}
		return data;
	}

	public static JSONObject getJSON(File file) {
		String combinedData = StringConverter.combineAll(getFileData(file));
		return new JSONObject(combinedData);
	}

	public static JSONArray getJSONFileAsJSONArray(File file) {
		String combinedData = StringConverter.combineAll(getFileData(file));
		return new JSONArray(combinedData);
	}

}
