package org.canvacord.discord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CanvaCordNotification {

	private long channelID;
	private List<CanvaCordRole> rolesToPing;

	private JSONObject schedule;
	private String messageFormat;

	public CanvaCordNotification(long channelID, List<CanvaCordRole> rolesToPing, JSONObject schedule, String messageFormat) {
		this.channelID = channelID;
		this.rolesToPing = rolesToPing;
		this.schedule = schedule;
		this.messageFormat = messageFormat;
	}

	public CanvaCordNotification(JSONObject configJSON) {
		this.channelID = configJSON.getLong("channel_id");
		this.schedule = configJSON.getJSONObject("schedule");
		this.messageFormat = configJSON.getString("message_format");
		readRolesFromJSON(configJSON.getJSONArray("roles"));
	}

	public long getChannelID() {
		return channelID;
	}

	public List<CanvaCordRole> getRolesToPing() {
		return rolesToPing;
	}

	public void setChannelID(long channelID) {
		this.channelID = channelID;
	}

	public void setRolesToPing(List<CanvaCordRole> rolesToPing) {
		this.rolesToPing = rolesToPing;
	}

	public JSONObject getSchedule() {
		return schedule;
	}

	public void setSchedule(JSONObject schedule) {
		this.schedule = schedule;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	private void readRolesFromJSON(JSONArray rolesArray) {
		rolesToPing = new ArrayList<>();
		for (Object obj : rolesArray.toList()) {
			rolesToPing.add(new CanvaCordRole((JSONObject) obj));
		}
	}

}
