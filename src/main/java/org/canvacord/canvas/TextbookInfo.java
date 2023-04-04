package org.canvacord.canvas;

import org.canvacord.main.CanvaCord;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.file.TextbookDirectory;
import org.canvacord.util.input.UserInput;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public record TextbookInfo(JSONObject textbookJSON) {

	public File getTextbookFile() {
		if (textbookJSON.has("file_path") && textbookJSON.has("file_name"))
			CanvaCord.explode("Bad TextbookInfo data");
		if (textbookJSON.has("file_path")) {
			return Paths.get(textbookJSON.getString("file_path")).toFile();
		}
		else if (textbookJSON.has("file_name")) {
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

	public TextbookInfo storeAndConvert(String instanceID) {
		File file = getTextbookFile();
		Optional<File> storedFile =	TextbookDirectory.storeTextbook(instanceID, file);
		if (storedFile.isPresent()) {
			JSONObject textbookData = new JSONObject();
			textbookData.put("title", getTitle());
			textbookData.put("author", getAuthor());
			textbookData.put("file_name", storedFile.get().getName());
			return new TextbookInfo(textbookData);
		}
		else {
			UserInput.showWarningMessage("Failed to store textbook:\n" + getTitle(), "Textbook Error");
			return null;
		}
	}

}
