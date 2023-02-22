package org.canvacord.canvas;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AnnouncementReader;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.interfaces.ModuleReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.Module;
import edu.ksu.canvas.model.announcement.Announcement;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.requestOptions.*;
import org.canvacord.persist.ConfigManager;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CanvasApi {

	private static CanvasApi instance;

	private final OauthToken TOKEN;
	private final CanvasApiFactory API;

	//constructor, made it not private for testing ]
	public CanvasApi(String canvasURL, String tokenStr) {
		TOKEN = new NonRefreshableOauthToken(tokenStr);
		API = new CanvasApiFactory(canvasURL);
	}



	public static CanvasApi getInstance() {
		if (instance == null) {
			JSONObject config = ConfigManager.getConfig();
			String url = config.getString("url");
			String token = config.getString("canvas_token");
			instance = new CanvasApi(url, token);
		}
		return instance;
	}

	public static boolean testCanvasInfo(String url, String userID, String tokenStr) {

		CanvasApi testInstance = new CanvasApi(url, tokenStr);

		try {
			testInstance.getCourses(userID);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

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
	public List<Course> getCourses(String userID) throws IOException {

		CourseReader reader = API.getReader(CourseReader.class, TOKEN);

		ListUserCoursesOptions options = new ListUserCoursesOptions(userID);

		return reader.listUserCourses(options);

	}

	public Optional<Course> getCourse(String courseID) throws IOException {

		CourseReader reader = API.getReader(CourseReader.class, TOKEN);

		GetSingleCourseOptions options = new GetSingleCourseOptions(courseID);

		return reader.getSingleCourse(options);

	}

	public List<Assignment> getAssignments(String courseID) throws IOException {

		// get an assignment reader
		AssignmentReader reader = API.getReader(AssignmentReader.class, TOKEN);

		ListCourseAssignmentsOptions options = new ListCourseAssignmentsOptions(courseID);

		return reader.listCourseAssignments(options);

	}

	public List<Module> getModules(Long courseID) throws IOException {

		// get a module reader
		ModuleReader reader = API.getReader(ModuleReader.class, TOKEN);

		ListModulesOptions options = new ListModulesOptions(courseID);

		return reader.getModulesInCourse(options);

	}

	public List<Announcement> getAnnouncements(String courseID) throws IOException {

		AnnouncementReader reader = API.getReader(AnnouncementReader.class, TOKEN);

		ListCourseAnnouncementsOptions options = new ListCourseAnnouncementsOptions(courseID);

		return reader.listCourseAnnouncements(options);

	}

}
