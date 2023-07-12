package org.canvacord.canvas;

import edu.ksu.canvas.model.Module;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class ModuleFetcher {

	protected static JSONArray getModuleFiles(List<Module> modules, String token) throws IOException {

		JSONArray allModules = getAllModuleItems(modules, token);
		discoverLinkedEntities(allModules, token);
		return allModules;

	}

	private static JSONArray getAllModuleItems(List<Module> modules, String token) throws IOException {

		JSONArray moduleItems = new JSONArray();

		for (int i = 0; i < modules.size(); i++) {

			//make http call
			StringBuffer response = CanvasApi.httpRequest(modules.get(i).getItemsUrl().toString(), token);

			// Put url in JSON Array Object
			JSONArray jsonArr = new JSONArray(response.toString());

			// Add JSON object to allModules array
			for (int j = 0; j < jsonArr.length(); j++) {
				moduleItems.put(jsonArr.getJSONObject(j));
			}

		}

		return moduleItems;

	}

	private static void discoverLinkedEntities(JSONArray moduleItems, String token) throws IOException {

		// Get one level deeper into the downloadable link url
		for (int i = 0; i < moduleItems.length(); i++) {

			// if module is a file type then we need to make another http call in order to get download link
			if (moduleItems.getJSONObject(i).get("type").toString().equals("File")) {

				StringBuffer response = CanvasApi.httpRequest(moduleItems.getJSONObject(i).get("url").toString(), token);

				JSONObject json = new JSONObject(response.toString());
				//add title to json object
				json.put("title", moduleItems.getJSONObject(i).get("title").toString());
				//replace with new json object containing downloadable url
				moduleItems.put(i, json);
			}
			// if json object has page url, then add a regular url
			else if (moduleItems.getJSONObject(i).has("page_url")) {
				moduleItems.getJSONObject(i).put("url", moduleItems.getJSONObject(i).get("html_url").toString());
			}
			// if json object has a html url, then add a regular url
			else if (moduleItems.getJSONObject(i).has("external_url")) {
				moduleItems.getJSONObject(i).put("url", moduleItems.getJSONObject(i).get("external_url").toString());
			}
		}

	}

}
