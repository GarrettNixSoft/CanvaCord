package org.canvacord.gui;

import java.util.HashMap;
import java.util.Map;

public class GuiDataStore {

	private final Map<String, Object> dataStore = new HashMap<>();

	public void store(String key, Object data) {
		dataStore.put(key, data);
	}

	public Object get(String key) {
		return dataStore.get(key);
	}

	public boolean has(String key) {
		return dataStore.containsKey(key);
	}

}
