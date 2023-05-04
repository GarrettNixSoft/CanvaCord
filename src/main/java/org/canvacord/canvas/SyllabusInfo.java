package org.canvacord.canvas;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;

public record SyllabusInfo(JSONObject syllabusJSON) {

	public SyllabusInfo(JSONObject syllabusJSON) {
		this.syllabusJSON = syllabusJSON;
	}

	public File getSyllabusFile() {
		String path = syllabusJSON.getString("file_path");
		return Paths.get(path).toFile();
	}
	public String getFileSize(){
		return syllabusJSON.getString("file_size");
	}
	public Date getLastModified(){
		return (Date) syllabusJSON.get("last_modified");
	}

	// TODO: examine the possibility of extracting other information such as grading scales, meeting times, etc.

}
