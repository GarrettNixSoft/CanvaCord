package org.canvacord.canvas;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;

public class SyllabusInfo {

	private final JSONObject syllabusJSON;

	public SyllabusInfo(JSONObject syllabusJSON) {
		this.syllabusJSON = syllabusJSON;
	}

	public File getSyllabusFile() {
		String path = syllabusJSON.getString("file_path");
		return Paths.get(path).toFile();
	}

	// TODO: examine the possibility of extracting other information such as grading scales, meeting times, etc.

}
