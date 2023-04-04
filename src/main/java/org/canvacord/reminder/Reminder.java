package org.canvacord.reminder;

import org.json.JSONObject;

import java.time.LocalDateTime;

public record Reminder(long userID, long channelID, LocalDateTime triggerDate, String message) {

	public static Reminder load(JSONObject reminderData) {
		long userID = reminderData.getLong("user_id");
		long channelID = reminderData.getLong("channel_id");
		LocalDateTime triggerDate = LocalDateTime.parse(reminderData.getString("trigger_date"));
		String message = reminderData.getString("message");
		// TODO if encrypted, decrypt
		return new Reminder(userID, channelID, triggerDate, message);
	}

	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		result.put("user_id", userID);
		result.put("channel_id", channelID);
		result.put("trigger_date", triggerDate.toString());
		result.put("message", message);
		return result;
	}

}
