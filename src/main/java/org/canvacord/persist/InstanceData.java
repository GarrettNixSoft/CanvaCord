package org.canvacord.persist;

import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.util.data.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.text.DateFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InstanceData {

	private final Instance owner;
	private final Map<CanvaCordNotification, CanvasEntityPool> entityPools;
	private final Map<Long, Date> cachedDueDates;

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MMM-dd-yyyy hh:mm");

	protected InstanceData(Instance owner, JSONObject instanceData) {
		this.owner = owner;
		entityPools = new HashMap<>();
		cachedDueDates = new HashMap<>();
		initEntityPools(instanceData);
	}

	private void initEntityPools(JSONObject instanceData) {
		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {
			String notificationName = notification.getName();
			CanvasEntityPool entityPool = new CanvasEntityPool(notificationName, instanceData);
			entityPools.put(notification, entityPool);
		}
		JSONObject dueDates = instanceData.getJSONObject("due_dates");
		for (String idStr : dueDates.keySet()) {
			long id = Long.parseLong(idStr);
			Date date;
			try {
				date = FORMATTER.parse(dueDates.getString(idStr));
				cachedDueDates.put(id, date);
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
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
		JSONObject dueDates = new JSONObject();
		for (long id : cachedDueDates.keySet()) {
			if (cachedDueDates.get(id) == null) continue;
			dueDates.put("" + id, FORMATTER.format(cachedDueDates.get(id)));
		}
		result.put("due_dates", dueDates);
		return result;
	}

	public Set<Long> getNewEntities(CanvaCordNotification notification) {
		return entityPools.get(notification).getNewEntities();
	}

	public Set<Long> getOldEntities(CanvaCordNotification notification) {
		return entityPools.get(notification).getOldEntities();
	}

	public Map<Long, Date> getCachedDueDates() {
		return cachedDueDates;
	}

	// ================ OPERATIONS ================
	public void processAssignment(long assignmentID) {
		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {
			entityPools.get(notification).processEntity(assignmentID);
		}
	}

	public void moveAssignment(long assignmentID, CanvaCordNotification notification) {
		entityPools.get(notification).moveToOld(assignmentID);
	}

	public void processAnnouncement(long announcementID) {
		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {
			entityPools.get(notification).processEntity(announcementID);
		}
	}

	public void moveAnnouncement(long announcementID, CanvaCordNotification notification) {
		entityPools.get(notification).moveToOld(announcementID);
	}

}
