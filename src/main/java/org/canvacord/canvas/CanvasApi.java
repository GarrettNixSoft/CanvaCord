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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
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
			String url = ConfigManager.getCanvasURL();
			String token = ConfigManager.getCanvasToken();
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

		return getCourseIncludes(courseID,null);

	}

	public Optional<Course> getCourseIncludes(String courseID, List<GetSingleCourseOptions.Include> includes) throws IOException {

		CourseReader reader = API.getReader(CourseReader.class, TOKEN);

		GetSingleCourseOptions options = new GetSingleCourseOptions(courseID);

		if (includes != null)
			options.includes(includes);

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

	// fetch assignments by data range
	public List<Assignment> getAssignmentsByDateRange(String courseID, Date startDate, Date endDate) throws IOException {

		// get an assignment reader
		AssignmentReader reader = API.getReader(AssignmentReader.class, TOKEN);

		ListCourseAssignmentsOptions options = new ListCourseAssignmentsOptions(courseID);
		List<Assignment> assignments = reader.listCourseAssignments(options);

		// check if assignments have a due date
		for(int i = 0; i < assignments.size(); i++) {
			// if not remove them from the list
			if(assignments.get(i).getDueAt() == null) {
				//remove assignment from list
				assignments.remove(i);
				//decrement i
				i--;
			}
			// else if element is within range, keep it in list
			else if(assignments.get(i).getDueAt().after(startDate) && assignments.get(i).getDueAt().before(endDate)) {

			}
			//else remove from list
			else {
				assignments.remove(i);
				//decrement i
				i--;
			}

		}
		// return reader.listCourseAssignments(options);
		return assignments;

	}
	// fetch modules, return json array with all downloadable files
	public JSONArray getDownloadableModules(Long courseID, String tokenStr) throws IOException {

		// get a module reader
		ModuleReader reader = API.getReader(ModuleReader.class, TOKEN);
		ListModulesOptions options = new ListModulesOptions(courseID);
		List<Module> modules = reader.getModulesInCourse(options);

		// will hold all downloadable module json objects
		JSONArray downloadableModules = new JSONArray();

		//Holds all the urls that are in different arrays
		List<String> urls = new ArrayList<>();

		//add urls from modules
		for(int i = 0; i < modules.size(); i++) {
			urls.add(modules.get(i).getItemsUrl().toString());
		}

		//FOR LOOP ALL THIS

		for (int i = 0; i < urls.size(); i++) {
			// Create URL Object
			URL obj = new URL(urls.get(i));

			// Create HttpURLConnection Object
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// Set RequestMethod and Request Property
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// Use token for authorization
			con.setRequestProperty("Authorization", "Bearer " + tokenStr);

			// Get Response to verify whether authentication was successful
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + urls.get(i).toString());
			System.out.println("Response Code : " + responseCode);

			// Read information from URL with BufferedReader Object
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Print as a string
			System.out.println(response.toString());

			// Put url in JSON Array Object
			JSONArray jsonArr = new JSONArray(response.toString());

			// Print JSON Object
			for (int j = 0; j < jsonArr.length(); j++) {
				// if jsonObj is a file throw it into the downloadableModules JSON Array
				JSONObject jsonObj = jsonArr.getJSONObject(j);
				// if object is a file then add it to array
				if (jsonArr.getJSONObject(j).get("type").toString().equals("File")) {
					downloadableModules.put(jsonArr.getJSONObject(j));
				}
			}
		}

		for(int i = 0; i < downloadableModules.length(); i++) {
			System.out.println(downloadableModules.getJSONObject(i));
		}

		// Get one level deeper into the downloadable link url

		for(int i = 0; i < downloadableModules.length(); i++) {
			// For simplification
			String url = downloadableModules.getJSONObject(i).get("url").toString();

			// Create URL Object
			URL obj = new URL(url);

			// Create HttpURLConnection Object
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// Set RequestMethod and Request Property
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// Use token for authorization
			con.setRequestProperty("Authorization", "Bearer " + tokenStr);

			// Get Response to verify whether authentication was successful
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + downloadableModules.getJSONObject(i).get("url").toString());
			System.out.println("Response Code : " + responseCode);

			// Read information from URL with BufferedReader Object
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine2;
			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Print as a string
			System.out.println(response.toString());

			JSONObject json = new JSONObject(response.toString());
			//replace with new json object containing downloadable url
			downloadableModules.put(i, json);
		}

		for(int i = 0; i < downloadableModules.length(); i++) {
			System.out.println(downloadableModules.getJSONObject(i));
		}


		return downloadableModules;

	}

	// DO NOT DELETE CODE BELOW!
	/*
	String canvasBaseUrl = "https://csulb.instructure.com/";

	// Get Path of Canvas Token
	Path fileName = Path.of("config/Canvas_Token.txt");

	// read the file
	String token = Files.readString(fileName);

	// Use CSULB url and token to make CanvasAPI object
	CanvasApi canvasApi = new CanvasApi(canvasBaseUrl, token);

	// Print Module Urls with course ID
	List<Module> modules = canvasApi.getModules(32109L);
        for(int i = 0; i < modules.size(); i++) {
		System.out.println(modules.get(i).getItemsUrl());
	}

	// Test to request information from Canvas Module URL
	// For simplification
	String url = modules.get(1).getItemsUrl().toString();

	// Create URL Object
	URL obj = new URL(url);

	// Create HttpURLConnection Object
	HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	// Set RequestMethod and Request Property
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	// Use token for authorization
        con.setRequestProperty("Authorization", "Bearer "+ token);

	// Get Response to verify whether authentication was successful
	int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

	// Read information from URL with BufferedReader Object
	BufferedReader in = new BufferedReader(
			new InputStreamReader(con.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
	}
        in.close();

	// Print as a string
        System.out.println(response.toString());

	// Put url in JSON Array Object
	JSONArray jsonArr = new JSONArray(response.toString());

	// Initialize JSON Object
        for (int i = 0; i < jsonArr.length(); i++)
	{
		JSONObject jsonObj = jsonArr.getJSONObject(i);
		System.out.println(jsonObj);
	}

	// Print URL
        System.out.println(jsonArr.getJSONObject(0).get("url"));

	// REQUEST NUMBER 2

	// For simplification
	url = jsonArr.getJSONObject(0).get("url").toString();

	// Create URL Object
	obj = new URL(url);

	// Create HttpURLConnection Object
	con = (HttpURLConnection) obj.openConnection();

	// Set RequestMethod and Request Property
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	// Use token for authorization
        con.setRequestProperty("Authorization", "Bearer "+ token);

	// Get Response to verify whether authentication was successful
	responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + jsonArr.getJSONObject(0).get("url"));
        System.out.println("Response Code : " + responseCode);

	// Read information from URL with BufferedReader Object
	in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
	String inputLine2;
	response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
	}
        in.close();

	// Print as a string
        System.out.println(response.toString());

	JSONObject json = new JSONObject(response.toString());

	// Print Download URL
        System.out.println(json.get("url"));

	//DOWNLOAD REQUEST!!!!

	// Download URL, contains the file
	url = json.get("url").toString();

	// Print name of file
        System.out.println(json.get("display_name").toString());

	// Create URL Object, redundant (will clean up later) just for simplification
	obj = new URL(url);

	// Create HttpURLConnection Object
	con = (HttpURLConnection) obj.openConnection();

	// Set RequestMethod and Request Property
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	// Use token for authorization
        con.setRequestProperty("Authorization", "Bearer "+ token);

	// Get Response to verify whether authentication was successful
	responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

	//VERIFIED? NOW TRY SAVING FILE

	// Requesting input data from server
	// con.getInputStream();

	// Initialize inputStream
	InputStream inputStream = null;

	// Initialize OutputStream
	OutputStream outputStream = null;

	// Where to save data?
	outputStream = new FileOutputStream("C://Users/frive/Documents/CanvaCord/config/" + json.get("display_name").toString());

	//Getting content Length
	int contentLength = con.getContentLength();
        System.out.println("File contentLength = " + contentLength + " bytes");

	// Requesting input data from server
	inputStream = con.getInputStream();

	// Limiting byte written to file per loop
	byte[] buffer = new byte[2048];

	// Increments file size
	int length;
	int downloaded = 0;

	// Looping until server finishes
        while ((length = inputStream.read(buffer)) != -1)
	{
		// Writing data
		outputStream.write(buffer, 0, length);
		downloaded+=length;
		//System.out.println("Download Status: " + (downloaded * 100) / (contentLength * 1.0) + "%");
	}

	// Close both streams
        outputStream.close();
        inputStream.close();


	 */

}
