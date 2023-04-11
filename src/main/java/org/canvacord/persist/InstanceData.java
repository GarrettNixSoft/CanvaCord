package org.canvacord.persist;

import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.util.data.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InstanceData {

	private final Instance owner;
	private final Map<CanvaCordNotification, CanvasEntityPool> entityPools;

	protected InstanceData(Instance owner, JSONObject instanceData) {
		this.owner = owner;
		entityPools = new HashMap<>();
		initEntityPools(instanceData);
	}

	private void initEntityPools(JSONObject instanceData) {
		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {
			String notificationName = notification.getName();
			CanvasEntityPool entityPool = new CanvasEntityPool(notificationName, instanceData);
			entityPools.put(notification, entityPool);
		}
	}

	// ================ GETTERS ================
	protected JSONObject getJSON() {
		JSONObject result = new JSONObject();
		for (CanvaCordNotification key : entityPools.keySet()) {
			JSONArray[] entityArrays = entityPools.get(key).toJSONArrays();
			result.put(key.getName() + "_new", entityArrays[0]);
			result.put(key.getName() + "_old", entityArrays[1]);
		}
		return result;
	}

	public Set<Long> getNewEntities(CanvaCordNotification notification) {
		return entityPools.get(notification).getNewEntities();
	}

	public Set<Long> getOldEntities(CanvaCordNotification notification) {
		return entityPools.get(notification).getOldEntities();
	}

	// ================ OPERATIONS ================
	public void processAssignment(long assignmentID) {
		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {
			entityPools.get(notification).discoverNew(assignmentID);
		}
	}

	public void moveAssignment(long assignmentID, CanvaCordNotification notification) {
		entityPools.get(notification).moveToOld(assignmentID);
	}

	public void processAnnouncement(long announcementID) {
		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {
			entityPools.get(notification).discoverNew(announcementID);
		}
	}

	public void moveAnnouncement(long announcementID, CanvaCordNotification notification) {
		entityPools.get(notification).discoverNew(announcementID);
	}

}
