package org.canvacord.persist;

import org.canvacord.exception.CanvaCordException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CanvasEntityPool {

	private final String notificationName;
	private final Set<Long> newEntities;
	private final Set<Long> oldEntities;

	public CanvasEntityPool(String notificationName, JSONObject entityData) {
		this.notificationName = notificationName;
		this.newEntities = new HashSet<>();
		this.oldEntities = new HashSet<>();
		initData(entityData);
	}

	private void initData(JSONObject entityData) {
		JSONArray newEntityData = entityData.optJSONArray(notificationName + "_new");
		if (newEntityData == null) {
			// TODO warning
			newEntityData = new JSONArray();
		}
		for (int i = 0; i < newEntityData.length(); i++)
			newEntities.add(newEntityData.getLong(i));
		JSONArray oldEntityData = entityData.optJSONArray(notificationName + "_old");
		if (oldEntityData == null) {
			// TODO warning
			oldEntityData = new JSONArray();
		}
		for (int i = 0; i < oldEntityData.length(); i++)
			newEntities.add(oldEntityData.getLong(i));
	}

	public void discoverNew(long entityID) {
		if (!newEntities.add(entityID))
			throw new CanvaCordException("Entity " + entityID + " is not new!");
	}

	public Set<Long> getNewEntities() {
		return Collections.unmodifiableSet(newEntities);
	}

	public Set<Long> getOldEntities() {
		return Collections.unmodifiableSet(oldEntities);
	}

	public void moveToOld(long entityID) {
		if (!newEntities.remove(entityID))
			throw new CanvaCordException("Entity " + entityID + " not found!");
		if (!oldEntities.add(entityID))
			throw new CanvaCordException("Entity " + entityID + " is already in the old pool!");
	}

	public JSONArray[] toJSONArrays() {
		JSONArray newEntityArray = new JSONArray();
		for (long id : newEntities)
			newEntityArray.put(id);
		JSONArray oldEntityArray = new JSONArray();
		for (long id : oldEntities)
			oldEntityArray.put(id);
		return new JSONArray[] {newEntityArray, oldEntityArray};
	}

}
