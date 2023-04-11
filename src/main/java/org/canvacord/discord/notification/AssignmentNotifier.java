package org.canvacord.discord.notification;

import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.entity.CanvaCordNotification;

import java.util.List;

public class AssignmentNotifier {

	/**
	 * Send notifications to users about newly posted assignments.
	 * @param notificationConfig a notification configured by the Owner which specifies
	 *                           the target channel, roles to ping, and message format
	 *                           to be used
	 * @param assignments a list of assignments found on Canvas
	 * @return {@code true} if all notifications are sent successfully, or false otherwise
	 */
	public static boolean notifyNewAssignments(CanvaCordNotification notificationConfig, List<Assignment> assignments) {
		// TODO
		return false;
	}

	/**
	 * Send notifications to users about changed due dates.
	 * @param notificationConfig a notification configured by the Owner which specifies
	 *                           the target channel, roles to ping, and message format
	 *                           to be used
	 * @param assignments the assignments whose due dates were changed
	 * @return {@code true} if the notification is sent successfully, or false otherwise
	 */
	public static boolean notifyDueDateChanged(CanvaCordNotification notificationConfig, List<Assignment> assignments) {
		// TODO
		return false;
	}

	/**
	 * Send notifications to users about approaching assignment due dates.
	 * @param notificationConfig a notification configured by the Owner which specifies
	 *                           the target channel, roles to ping, and message format
	 *                           to be used
	 * @param assignments the assignment that is due soon
	 * @return {@code true} if the notification is sent successfully, or false otherwise
	 */
	public static boolean notifyDueDateApproaching(CanvaCordNotification notificationConfig, List<Assignment> assignments) {
		// TODO
		return false;
	}

}
