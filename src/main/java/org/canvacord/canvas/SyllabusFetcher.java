package org.canvacord.canvas;

import org.json.JSONObject;

import java.util.Optional;

public class SyllabusFetcher {

	protected static Optional<SyllabusInfo> fetchSyllabusForCourse(String courseID) {

		// TODO:
		// 0. Check if there's already a valid object for this course, and if so, return it immediately
		// 1. Fetch the course from Canvas
		// 2. Get its syllabus_body
		// 3. If the syllabus_body is present and valid, search for a file link of type PDF or DOC (.doc, .docx, etc.)
		// 4. If a file link is found, attempt to download the file
		// 5. If the download succeeds, send the file through the appropriate parser for its file type
		// 6. If the parser retrieves good data, write it to disk and return the object

		return Optional.empty();
	}

}
