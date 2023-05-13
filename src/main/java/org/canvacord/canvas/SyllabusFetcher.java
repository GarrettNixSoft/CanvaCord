package org.canvacord.canvas;

import edu.ksu.canvas.exception.ObjectNotFoundException;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.Module;
import edu.ksu.canvas.requestOptions.GetSingleCourseOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.util.net.RemoteFileGetter;
import org.checkerframework.checker.nullness.Opt;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.text.html.Option;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.List;

import static org.canvacord.util.file.CanvaCordPaths.getInstanceDirPath;


public class SyllabusFetcher {

	private static Path syllabusPath;
	private static final String[] validExtensions = {"txt","pdf", "doc","docx"};
	private static final String token = ConfigManager.getCanvasToken();
	private static final String canvasBaseUrl = ConfigManager.getCanvasURL();
	private static final Logger LOGGER = LogManager.getLogger(SyllabusFetcher.class);


	/**
	 * Searches Canvas for a syllabus posted under the given course.
	 * @param courseID the ID of the Canvas course to search
	 * @return an Optional type containing a SyllabusInfo object describing what
	 * was found on Canvas, or empty if nothing was found.
     **/
	protected static Optional<SyllabusInfo> fetchSyllabusForCourse(String courseID) {

		Instance instanceForCourse = InstanceManager.getInstanceByCourseID(courseID).orElseThrow(()-> new CanvaCordException("Instance not found"));
		syllabusPath = getInstanceDirPath(instanceForCourse);
		Optional<File> instanceFile = findSyllabusFromInstance(instanceForCourse);
		JSONObject syllabusJSON;
		File syllabus;

		try {
			syllabus = (instanceFile.isPresent())?
					instanceFile.get():
					findSyllabusFromCanvas(courseID).orElseThrow(IOException::new);

			syllabusJSON = buildJSON(syllabus);
			//HERE IS WHERE WE WOULD DO THE SCRAPING
		}
		catch(IOException e){
			LOGGER.debug("Syllabus fetching failed.");
			return Optional.empty();
		}

		// Check for file validity
		if (!FileUtil.isValidFile(syllabus,validExtensions)){
			LOGGER.debug("Syllabus file is of invalid extension.");
			// return it anyway
		}

		return (syllabusJSON.isEmpty())? Optional.empty(): Optional.of(new SyllabusInfo(syllabusJSON));
	}

	protected static Optional<SyllabusInfo> fetchSyllabusFromCanvas(String courseID){
		System.out.println(courseID);
		Instance instanceForCourse;
		try{
			instanceForCourse = InstanceManager.getInstanceByCourseID(courseID).orElseThrow(()-> new CanvaCordException("Instance not found"));
		}
		catch (Exception e){
			return Optional.empty();
		}
		syllabusPath = getInstanceDirPath(instanceForCourse);
		File syllabus;
		JSONObject syllabusJSON;

		try{
			syllabus = findSyllabusFromCanvas(courseID).orElseThrow(IOException::new);
			syllabusJSON = buildJSON(syllabus);
		}
		catch (IOException e){
			return Optional.empty();
		}

		if (!FileUtil.isValidFile(syllabus,validExtensions)){
			LOGGER.debug("Syllabus file is of invalid extension.");
		}

		return (syllabusJSON.isEmpty())? Optional.empty(): Optional.of(new SyllabusInfo(syllabusJSON));
	}

	private static Optional<File> findSyllabusFromInstance(Instance instance){
		if (!instance.hasSyllabus()){
			return Optional.empty();
		}
		syllabusPath = getInstanceDirPath(instance);

		FilenameFilter filter = (f,name) -> (name.startsWith("syllabus."));

		File[] syllabusFiles = syllabusPath.toFile().listFiles(filter);

		if (syllabusFiles!= null && syllabusFiles.length == 1){
			if (FileUtil.isValidFile(syllabusFiles[0], validExtensions))
				return Optional.of(syllabusFiles[0]);
		}
		return Optional.empty();
	}
	private static Optional<File> findSyllabusFromCanvas(String courseID) throws IOException {
		CanvasApi canvasApi = CanvasApi.getInstance();
		String syllabusBody = null;
		List<Module> modules = canvasApi.getModules(courseID); // to be changed when modules class implemented
		JSONObject downloadJSON;
		String dataApiReturnType = null;
		String dataApiEndpoint = null;


		try {
			syllabusBody = getSyllabusBody(canvasApi, courseID);
			dataApiReturnType = extractFromHtml(syllabusBody,"data-api-returntype");
			dataApiEndpoint = extractFromHtml(syllabusBody,"data-api-endpoint");
		}
		catch(IOException e){
			LOGGER.error("Error fetching specified course.");
		}
		catch (ObjectNotFoundException ignore){
			// there is no syllabus body for the course
		}

		// from the HTML extracted in the syllabus body, if there is a link to another page
		//
		// https://canvas.instructure.com/doc/api/file.endpoint_attributes.html

		if (dataApiReturnType == null || dataApiEndpoint == null) { //todo use modules class methods

			Optional<Module> syllabusModule = modules.stream()
					.filter(module -> module.getName().equalsIgnoreCase("syllabus")).findFirst();
			// only finds if the name EQUALS syllabus, not accounting for if contains the word
			// ^ there's also the /?search_term=term that could replace this?

			if(syllabusModule.isPresent()){
				dataApiEndpoint = canvasBaseUrl +"api/v1/courses/"+  courseID + "/modules/"+ syllabusModule.get().getId();

				dataApiReturnType = "Module";

			} else { // there is no syllabus body, no link on page, and no syllabus listed under modules
				return Optional.empty();
			}
		}

		switch (dataApiReturnType) {
			case "File" -> downloadJSON = getJSONfromURL(dataApiEndpoint);
			case "Module" -> {

				JSONObject moduleItems = getJSONfromURL(dataApiEndpoint);
				if ((int) moduleItems.get("items_count") != 1) return Optional.empty(); // option: if >1, could let user pick
				JSONObject moduleSyllabus = getJSONfromURL(moduleItems.get("items_url").toString());
				downloadJSON = getJSONfromURL(moduleSyllabus.get("url").toString());
			}
			default -> { //option: find out how page and folders work?
				return Optional.empty(); //is: Assignment, Discussion, Quiz, SessionlessLaunchUrl , Page, Folder, and lists.
			}
		}


		return downloadFile(downloadJSON);
	}

	private static JSONObject buildJSON(File syllabus) throws IOException {
		return new JSONObject()
				.put("file_path",syllabus.getPath())
				.put("name",syllabus.getName())
				.put("file_size",String.format("%,d kilobytes", Files.size(syllabus.toPath()) / 1024))
				.put("last_modified",new Date(syllabus.lastModified()));
	}

	// ---------HELPER METHODS FOR FETCHING THE SYLLABUS OFF OF CANVAS--------
	private static String getSyllabusBody(CanvasApi canvasApi, String courseID) throws IOException {
		// Create a new Course object to access the specific course, specifying to include the Syllabus body
		// throws a io exception if the courseID doesn't correspond to a course
		Course course = canvasApi.getCourseIncludes(String.valueOf(courseID), Collections.singletonList(
				GetSingleCourseOptions.Include.SYLLABUS_BODY)).orElseThrow(IOException::new);

		return course.getSyllabusBody();
	}

	private static Optional<File> downloadFile(JSONObject fileDownloadJSON){
		Path filePath;
		String downloadURL = fileDownloadJSON.get("url").toString();
		String downloadType = "." + fileDownloadJSON.get("mime_class").toString();
		String downloadFileName = "syllabus" + downloadType;

		try{
			filePath = Path.of(syllabusPath.toString() + "\\" + downloadFileName);
		} catch (NoSuchElementException | NullPointerException e){
			UserInput.showWarningMessage("There was an error locating the Instance folder.","Error");
			return Optional.empty();
		}
		return RemoteFileGetter.downloadAndSave(downloadURL,filePath);
	}

	private static String extractFromHtml(String htmlPhrase, String keyPhrase){
		if (htmlPhrase == null) return null;
		int indexOfSubstring = htmlPhrase.indexOf(keyPhrase);
		if (indexOfSubstring == -1) return null;

		indexOfSubstring += keyPhrase.length() + 2; // need to get past keyPhrase, then '="'
		String phrase = htmlPhrase.substring(indexOfSubstring);
		phrase = phrase.substring(0,phrase.indexOf('"'));
		return phrase;
	}


	// code created to check the instance folder for existing files
	// needs to be adapted if going to be used

	/*
	protected static Optional<File> fetchSyllabusFromInstanceOwner (Instance instance) throws IOException {
		if (!instance.hasSyllabus()){
			return Optional.empty();
		}
		FilenameFilter filter = (f,name) -> (name.startsWith("syllabus."));

		File[] syllabusFiles = syllabusPath.toFile().listFiles(filter);

		// no syllabus file - means instance data is incorrect
		if (syllabusFiles == null || syllabusFiles.length == 0){
			UserInput.showWarningMessage("The instance data is incorrect, and will be changed. The syllabus file will now be re-fetched from Canvas","Warning");
			// change instance config has_syllabus = false
			return Optional.empty();
		}

		// more than 1 syllabus file
		else if (syllabusFiles.length >1){
			if(UserInput.askToConfirm("Multiple syllabus files detected for this instance. Would you like to delete them before re-fetching from Canvas?","Warning")){
				for (File syllabusFile : syllabusFiles) {
					if(!syllabusFile.delete()){
						System.err.println("Syllabus file deletion unsuccessful.");
					}
				}
			} else{ // user chose not to delete the extra files
				try{
					for(int i=0;i<syllabusFiles.length;i++){
						renameSyllabusFile(syllabusFiles[i],"("+i+")");
					}
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
			return Optional.empty();
		}

		// Invalid filetype
		else if (!FileUtil.isValidFile(syllabusFiles[0],validExtensions)){
			if (UserInput.askToConfirm("The syllabus file stored for the instance is of an invalid filetype. Would you like to delete it before re-fetching from Canvas?","Warning")){
				if (!syllabusFiles[0].delete()){
					System.err.println("Syllabus file deletion unsuccessful.");
				}
			} else{ // user chose not to delete the broken syllabus
				try{
					renameSyllabusFile(syllabusFiles[0],"_broken");
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
			return Optional.empty();
		}
		return Optional.of(syllabusFiles[0]);
	}

	private static void renameSyllabusFile(File toRename,String message) throws IOException {
		int dotIndex = toRename.getName().indexOf("syllabus.")+8;
		String[] oldName = {toRename.getName().substring(0,dotIndex),toRename.getName().substring(dotIndex)};
		String newName = oldName[0] + message + oldName[1];

		Files.move(toRename.toPath(),toRename.toPath().resolveSibling(newName),REPLACE_EXISTING);
	}
	*/

	//-----------------------------------------------------------------------------------------------------------------
	//
	//                      MOST OF THESE METHODS REALLY SHOULD BE SOMEWHERE ELSE
	//						I know some of them will be shared with module fetching
	//
	//-----------------------------------------------------------------------------------------------------------------


	// adapted from code written by Francisco
	private static JSONObject getJSONfromURL (String url) throws IOException {
		HttpURLConnection con = verifyConnection(url);

		// Read information from URL with BufferedReader Object
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return (response.charAt(0) == '[')?
				(JSONObject) new JSONArray(response.toString()).get(0) : new JSONObject(response.toString());
	}

	// method written by Francisco
	private static HttpURLConnection verifyConnection(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		// Use token for authorization
		con.setRequestProperty("Authorization", "Bearer "+ token);

		// Get Response to verify whether authentication was successful
		if (con.getResponseCode() != HttpURLConnection.HTTP_OK){
			throw new IOException("Authentication failed");
		}
		return con;
	}
}
