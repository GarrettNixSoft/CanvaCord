package org.canvacord.persist;

import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.util.data.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class InstanceData {

	private final Instance owner;
	private final JSONObject instanceData;

	protected InstanceData(Instance owner, JSONObject instanceData) {
		this.owner = owner;
		this.instanceData = instanceData;
	}

	// ================ GETTERS ================
	protected JSONObject getJSON() {return instanceData; }

	public JSONArray getPastAssignments(CanvaCordNotification notification) {
		return instanceData.getJSONArray(notification.getName() + "_past_assignments");
	}

	public JSONArray getNewAssignments(CanvaCordNotification notification) {
		return instanceData.getJSONArray(notification.getName() + "_new_assignments");
	}

	public JSONArray getPastAnnouncements(CanvaCordNotification notification) {
		return instanceData.getJSONArray(notification.getName() + "_past_announcements");
	}

	public JSONArray getNewAnnouncements(CanvaCordNotification notification) {
		return instanceData.getJSONArray(notification.getName() + "_new_announcements");
	}

	// ================ OPERATIONS ================
	public void processAssignment(long assignmentID) {

		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {

			// Fetch the old assignments list
			JSONArray oldAssignments = getPastAssignments(notification);

			// Search for this assignment ID in the old assignments list
			// If a match was found, do nothing
			if (JSONUtils.arrayContainsLong(oldAssignments, assignmentID)) return;

			// Otherwise, fetch the new assignment list
			JSONArray newAssignments = getNewAssignments(notification);

			// Search the new assignments for a match
			if (JSONUtils.arrayContainsLong(newAssignments, assignmentID)) return;

			// If neither array contained the ID, it's a new assignment, so add it to the new array
			newAssignments.put(assignmentID);

		}

	}

	public void moveAssignment(long assignmentID) {

		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {

			// Get both arrays
			JSONArray newAssignments = getNewAssignments(notification);
			JSONArray oldAssignments = getPastAssignments(notification);

			// Search the new one for the matching assignment ID
			boolean found = false;
			for (int i = 0; i < newAssignments.length(); i++) {
				long id = newAssignments.getLong(i);
				// If a match is found, move it to the old array and exit the loop
				if (id == assignmentID) {
					found = true;
					newAssignments.remove(i);
					oldAssignments.put(assignmentID);
					break;
				}
			}

			// If no match was found, something has gone wrong
			if (!found)
				throw new CanvaCordException("Assignment ID " + assignmentID + " not found in the cache!");
		}

	}

	public void processAnnouncement(long announcementID) {

		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {

			// Fetch the old announcements list
			JSONArray oldAnnouncements = getPastAnnouncements(notification);

			// Search for this announcement ID in the old announcements list
			// If a match was found, do nothing
			if (JSONUtils.arrayContainsLong(oldAnnouncements, announcementID)) return;

			// Otherwise, fetch the new announcement list
			JSONArray newAnnouncements = getNewAnnouncements(notification);

			// Search the new announcements for a match
			if (JSONUtils.arrayContainsLong(newAnnouncements, announcementID)) return;

			// If neither array contained the ID, it's a new announcement, so add it to the new array
			newAnnouncements.put(announcementID);

		}



	}

	public void moveAnnouncement(long announcementID) {

		for (CanvaCordNotification notification : owner.getConfiguredNotifications(false)) {

			// Get both arrays
			JSONArray newAnnouncements = getNewAnnouncements(notification);
			JSONArray oldAnnouncements = getPastAnnouncements(notification);

			// Search the new one for the matching announcement ID
			boolean found = false;
			for (int i = 0; i < newAnnouncements.length(); i++) {
				long id = newAnnouncements.getLong(i);
				// If a match is found, move it to the old array and exit the loop
				if (id == announcementID) {
					found = true;
					newAnnouncements.remove(i);
					oldAnnouncements.put(announcementID);
					break;
				}
			}

			// If no match was found, something has gone wrong
			if (!found)
				throw new CanvaCordException("Announcement ID " + announcementID + " not found in the cache!");
		}



	}

}
