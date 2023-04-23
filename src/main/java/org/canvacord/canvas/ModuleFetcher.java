package org.canvacord.canvas;

import edu.ksu.canvas.model.Module;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class ModuleFetcher {

	protected static JSONArray getModuleFiles(List<Module> modules, String token) throws IOException {

		// will hold all modules
		JSONArray allModules = new JSONArray();

		//FOR LOOP ALL THIS

		for (int i = 0; i < modules.size(); i++) {

			//make http call
			StringBuffer response = CanvasApi.httpRequest(modules.get(i).getItemsUrl().toString(), token);

			// Put url in JSON Array Object
			JSONArray jsonArr = new JSONArray(response.toString());

			// Add JSON object to allModules array
			for (int j = 0; j < jsonArr.length(); j++) {
				allModules.put(jsonArr.getJSONObject(j));
			}


		}

		// Get one level deeper into the downloadable link url
		for (int i = 0; i < allModules.length(); i++) {

			// if module is a file type then we need to make another http call in order to get download link
			if (allModules.getJSONObject(i).get("type").toString().equals("File")) {

				StringBuffer response = CanvasApi.httpRequest(allModules.getJSONObject(i).get("url").toString(), token);

				JSONObject json = new JSONObject(response.toString());
				//add title to json object
				json.put("title", allModules.getJSONObject(i).get("title").toString());
				//replace with new json object containing downloadable url
				allModules.put(i, json);
			}
			// if json object has page url, then add a regular url
			else if (allModules.getJSONObject(i).has("page_url")) {
				allModules.getJSONObject(i).put("url", allModules.getJSONObject(i).get("html_url").toString());
			}
			// if json object has a html url, then add a regular url
			else if (allModules.getJSONObject(i).has("external_url")) {
				allModules.getJSONObject(i).put("url", allModules.getJSONObject(i).get("external_url").toString());
			}
		}
		return allModules;

	}

}
