package org.canvacord.canvas;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;

public record TextbookInfo(JSONObject textbookJSON) {

	public File getTextbookFile() {
		if (textbookJSON.has("file_name")) {
			String name = textbookJSON.getString("file_name");
			String instanceID = name.substring(0, name.indexOf('_'));
			return Paths.get("instances/" + instanceID + "/" + name).toFile();
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
