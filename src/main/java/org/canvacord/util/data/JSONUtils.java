package org.canvacord.util.data;

import org.json.JSONArray;

public class JSONUtils {

	public static boolean arrayContainsLong(JSONArray array, long target) {
		for (int i = 0; i < array.length(); i++) {
			if (array.getLong(i) == target) {
				return true;
			}
		}
		return false;
	}

}
