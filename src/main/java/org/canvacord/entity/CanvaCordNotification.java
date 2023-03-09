package org.canvacord.entity;

import org.canvacord.event.CanvaCordEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CanvaCordNotification {

	private CanvaCordEvent.Type eventType;

	private long channelID;
	private List<CanvaCordRole> rolesToPing;

	private JSONObject schedule;
	private String messageFormat;

	private String friendlyScheduleDescription;

	public CanvaCordNotification(CanvaCordEvent.Type eventType, long channelID, List<CanvaCordRole> rolesToPing, JSONObject schedule, String messageFormat, String friendlyScheduleDescription) {
		this.eventType = eventType;
		this.channelID = channelID;
		this.rolesToPing = rolesToPing;
		this.schedule = schedule;
		this.messageFormat = messageFormat;
		this.friendlyScheduleDescription = friendlyScheduleDescription;
	}

	public CanvaCordNotification(JSONObject configJSON) {
		this.eventType = CanvaCordEvent.Type.stringToType(configJSON.getString("event_type"));
		this.channelID = configJSON.getLong("channel_id");
		this.schedule = configJSON.getJSONObject("schedule");
		this.messageFormat = configJSON.getString("message_format");
		this.friendlyScheduleDescription = configJSON.getString("friendly_schedule_desc");
		readRolesFromJSON(configJSON.getJSONArray("roles"));
	}

	// ================================ GETTERS ================================
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
	private void readRolesFromJSON(JSONArray rolesArray) {
		rolesToPing = new ArrayList<>();
		for (Object obj : rolesArray.toList()) {
			rolesToPing.add(new CanvaCordRole((JSONObject) obj));
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
		result.put("channel_id", channelID);
		result.put("roles", buildRolesArray());
		result.put("schedule", schedule);
		result.put("message_format", messageFormat);
		result.put("friendly_schedule_desc", friendlyScheduleDescription);
		return result;
	}

}
