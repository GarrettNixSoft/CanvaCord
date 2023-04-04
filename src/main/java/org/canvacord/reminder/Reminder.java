package org.canvacord.reminder;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public record Reminder(String reminderID, long userID, long channelID, LocalDateTime triggerDate, String message) {

	/**
	 * Builds a new Reminder using the given fields and with a random UUID.
	 * @param userID the Discord ID of the target user
	 * @param channelID the Discord ID of the target channel
	 * @param triggerDate when the reminder should be sent
	 * @param message the reminder message
	 * @return a new Reminder object
	 */
	public static Reminder buildNew(long userID, long channelID, LocalDateTime triggerDate, String message) {
		String reminderID = UUID.randomUUID().toString();
		return new Reminder(reminderID, userID, channelID, triggerDate, message);
	}

	public static Reminder load(JSONObject reminderData) {
		String reminderID = reminderData.getString("id");
		long userID = reminderData.getLong("user_id");
		long channelID = reminderData.getLong("channel_id");
		LocalDateTime triggerDate = LocalDateTime.parse(reminderData.getString("trigger_date"));
		String message = reminderData.getString("message");
		// TODO if encrypted, decrypt
		return new Reminder(reminderID, userID, channelID, triggerDate, message);
	}

	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		result.put("id", reminderID);
		result.put("user_id", userID);
		result.put("channel_id", channelID);
		result.put("trigger_date", triggerDate.toString());
		result.put("message", message);
		return result;
	}

	public Date getTriggerDateAsDate() {
		return Date.from(triggerDate.atZone(ZoneId.systemDefault()).toInstant());
	}

}
