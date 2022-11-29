package org.canvacord.canvas;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.string.StringConverter;
import org.canvacord.util.string.StringUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CanvasApi {

	private static CanvasApi instance;

	private final OauthToken TOKEN;
	private final CanvasApiFactory API;

	private CanvasApi(String canvasURL, String tokenStr) {
		TOKEN = new NonRefreshableOauthToken(tokenStr);
		API = new CanvasApiFactory(canvasURL);
	}

	public static CanvasApi getInstance() {
		if (instance == null) {
			// TODO: LOAD URL AND TOKEN FROM CONFIG
			String url = "csulb.instructure.com";
			String token = StringConverter.combineAll(FileUtil.getFileData(Paths.get("config/token-canvas.txt").toFile()));
			instance = new CanvasApi(url, token);
		}
		return instance;
	}

	// ******************************** CUSTOM SEARCHES ********************************
	public SyllabusInfo findSyllabus(String courseID) {

		// TODO:
		// 0. Check if there's already a valid object for this course, and if so, return it immediately
		// 1. Fetch the course from Canvas
		// 2. Get its syllabus_body
		// 3. If the syllabus_body is present and valid, search for a file link of type PDF or DOC (.doc, .docx, etc.)
		// 4. If a file link is found, attempt to download the file
		// 5. If the download succeeds, send the file through the appropriate parser for its file type
		// 6. If the parser retrieves good data, write it to disk and return the object

		// Fail state
		return null;

	}


	// ******************************** FETCHING CANVAS OBJECTS ********************************
	public List<Assignment> getAssignments(String courseID) {

		// get an assignment reader
		AssignmentReader reader = API.getReader(AssignmentReader.class, TOKEN);

		ListCourseAssignmentsOptions options = new ListCourseAssignmentsOptions(courseID);

		try {
			return reader.listCourseAssignments(options);
		}
		catch (IOException e) {
			// TODO: Warn Owner
			return new ArrayList<>();
		}

	}



}
