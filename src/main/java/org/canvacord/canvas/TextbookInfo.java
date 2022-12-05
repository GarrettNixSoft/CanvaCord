package org.canvacord.canvas;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;

public record TextbookInfo(JSONObject textbookJSON) {

	public File getTextbookFile() {
		if (textbookJSON.has("file_path")) {
			String path = textbookJSON.getString("file_path");
			return Paths.get(path).toFile();
		}
		else return null;
	}

	public String getTitle() {
		return textbookJSON.optString("title", "None Specified");
	}

	public String getAuthor() {
		return textbookJSON.optString("author", "None Specified");
	}

}
