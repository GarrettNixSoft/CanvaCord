package org.canvacord.util.data;

import org.json.JSONArray;

public class JSONUtils {

	public static boolean arrayContains(JSONArray array, Object target) {
		for (int i = 0; i < array.length(); i++) {
			if (array.get(i).equals(target)) {
				return true;
			}
		}
		return false;
	}

}
