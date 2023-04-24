package org.canvacord.reminder;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * A Reminder represents a message that a user has requested be sent to
 * them at a specified time in the future.
 * @param reminderID a randomly generated unique identifier for the reminder
 * @param userID the Discord ID of the user who requested the reminder
 * @param channelID the channel the reminder was generated in
 * @param createdAt the time the reminder was created
 * @param triggerDate the time when the reminder should be sent
 * @param encrypted whether the user requested their message be encrypted
 * @param message the message the user wants to be reminded with
 */
public record Reminder(String reminderID, long userID, long channelID, LocalDateTime createdAt, LocalDateTime triggerDate, boolean encrypted, String message) {

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
		return new Reminder(reminderID, userID, channelID, LocalDateTime.now(), triggerDate, false, message);
	}

	/**
	 * Load a Reminder from its JSON form stored on disk.
	 * @param reminderData a JSONObject generated by the {@code Reminder.toJSON()} method
	 * @return a Reminder object
	 */
	public static Reminder load(JSONObject reminderData) {
		String reminderID = reminderData.getString("id");
		long userID = reminderData.getLong("user_id");
		long channelID = reminderData.getLong("channel_id");
		LocalDateTime createdAt = LocalDateTime.parse(reminderData.getString("created_at"));
		LocalDateTime triggerDate = LocalDateTime.parse(reminderData.getString("trigger_date"));
		boolean encrypted = reminderData.getBoolean("encrypted");
		String message = reminderData.getString("message");
		// TODO if encrypted, decrypt
		return new Reminder(reminderID, userID, channelID, createdAt, triggerDate, encrypted, message);
	}

	/**
	 * Convert this Reminder to JSON format to store on disk.
	 * @return a JSONObject representation of this Reminder
	 */
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		result.put("id", reminderID);
		result.put("user_id", userID);
		result.put("channel_id", channelID);
		result.put("created_at", createdAt.toString());
		result.put("trigger_date", triggerDate.toString());
		result.put("encrypted", encrypted);
		result.put("message", message);
		return result;
	}

	public Date getTriggerDateAsDate() {
		return Date.from(triggerDate.atZone(ZoneId.systemDefault()).toInstant());
	}

}
