package org.canvacord.canvas;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.file.TextbookScraper;
import org.json.JSONObject;

import java.io.File;
import java.util.Optional;

public class TextbookFetcher {

	/**
	 * Attempts to find a copy of a textbook online.
	 * @param searchTerm the terms to use in searching for the textbook
	 * @return an Optional type containing information about what was found,
	 * or empty if no results were found.
	 */
	public static Optional<TextbookInfo> fetchTextbookOnline(String searchTerm, String courseID) {
		// TODO
		Instance instanceForCourse = InstanceManager.getInstanceByCourseID(courseID).orElseThrow(()->new CanvaCordException("Instance not found"));
		File textbook = TextbookScraper.downloadTextbook(instanceForCourse.getInstanceID(), searchTerm);
		JSONObject textbookData = new JSONObject();
		textbookData.put("title", instanceForCourse.getName());
		textbookData.put("author", "");
		textbookData.put("file_name", textbook.getName());
		return Optional.of(new TextbookInfo(textbookData));
	}

}
