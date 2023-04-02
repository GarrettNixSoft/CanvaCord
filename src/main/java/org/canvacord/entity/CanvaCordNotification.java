package org.canvacord.entity;

import org.canvacord.event.CanvaCordEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CanvaCordNotification {

	private String name;
	private CanvaCordEvent.Type eventType;

	private long channelID;
	private List<CanvaCordRole> rolesToPing;

	private JSONObject schedule;
	private String messageFormat;

	private String friendlyScheduleDescription;

	public CanvaCordNotification(String name, CanvaCordEvent.Type eventType, long channelID, List<CanvaCordRole> rolesToPing, JSONObject schedule, String messageFormat, String friendlyScheduleDescription) {
		this.name = name;
		this.eventType = eventType;
		this.channelID = channelID;
		this.rolesToPing = rolesToPing;
		this.schedule = schedule;
		this.messageFormat = messageFormat;
		this.friendlyScheduleDescription = friendlyScheduleDescription;
	}

	public CanvaCordNotification(JSONObject configJSON, JSONArray roleObjects) {
		this.name = configJSON.getString("name");
		this.eventType = CanvaCordEvent.Type.stringToType(configJSON.getString("event_type"));
		this.channelID = configJSON.getLong("channel_id");
		this.schedule = configJSON.getJSONObject("schedule");
		this.messageFormat = configJSON.getString("message_format");
		this.friendlyScheduleDescription = configJSON.getString("friendly_schedule_desc");
		readRolesFromJSON(configJSON.getJSONArray("roles"), roleObjects);
	}

	// ================================ GETTERS ================================
	public String getName() {
		return name;
	}

	public CanvaCordEvent.Type getEventType() {
		return eventType;
	}

	public long getChannelID() {
		return channelID;
	}

	public List<CanvaCordRole> getRolesToPing() {
		return rolesToPing;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public JSONObject getSchedule() {
		return schedule;
	}

	public String getFriendlyScheduleDescription() {
		return friendlyScheduleDescription;
	}

	// ================================ SETTERS ================================
	public void setName(String name) {
		this.name = name;
	}

	public void setEventType(CanvaCordEvent.Type eventType) {
		this.eventType = eventType;
	}

	public void setChannelID(long channelID) {
		this.channelID = channelID;
	}

	public void setRolesToPing(List<CanvaCordRole> rolesToPing) {
		this.rolesToPing = rolesToPing;
	}

	public void setSchedule(JSONObject schedule) {
		this.schedule = schedule;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public void setFriendlyScheduleDescription(String friendlyScheduleDescription) {
		this.friendlyScheduleDescription = friendlyScheduleDescription;
	}

	// ================================ UTILITY ================================
	private void readRolesFromJSON(JSONArray rolesArray, JSONArray roleObjects) {
		rolesToPing = new ArrayList<>();
		for (Object obj : rolesArray.toList()) {
			String roleName = (String) obj;
			for (int i = 0; i < roleObjects.length(); i++) {
    JSONObject roleData = roleObjects.getJSONObject(i);
    rolesToPing.add(new CanvaCordRole(roleData));
   }
		}
	}

	private JSONArray buildRolesArray() {
		JSONArray result = new JSONArray();
		for (CanvaCordRole role : rolesToPing) {
			result.put(role.getName());
		}
		return result;
	}

	public JSONObject getJSON() {
		JSONObject result = new JSONObject();
		result.put("name", name);
		result.put("channel_id", channelID);
		result.put("event_type", eventType.toString());
		result.put("roles", buildRolesArray());
		result.put("schedule", schedule);
		result.put("message_format", messageFormat);
		result.put("friendly_schedule_desc", friendlyScheduleDescription);
		return result;
	}

}
