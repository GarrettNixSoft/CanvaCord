package org.canvacord.canvas;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;

import java.util.List;
import java.util.Optional;

public class TextbookFetcher {

	/**
	 * Attempts to find a copy of a textbook online.
	 * @param searchTerm the terms to use in searching for the textbook
	 * @return an Optional type containing information about what was found,
	 * or empty if no results were found.
	 */
	public static Optional<TextbookInfo> fetchTextbookOnline(String searchTerm) {
		// TODO
		return Optional.empty();
	}

	public static List<TextbookInfo> fetchTextbook(String courseID) {
		Instance instanceForCourse = InstanceManager.getInstanceByCourseID(courseID).orElseThrow(()-> new CanvaCordException("Instance not found"));
		return instanceForCourse.getTextbooks();
	}

}
