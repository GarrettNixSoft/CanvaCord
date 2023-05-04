package org.canvacord.scheduler.job;

import edu.ksu.canvas.model.announcement.Announcement;
import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.discord.notification.CanvasNotifier;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.AssignmentFilter;
import org.canvacord.persist.CacheManager;
import org.canvacord.util.data.Pair;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.List;

public class NotificationJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap dataMap = context.getMergedJobDataMap();

        Instance instance = InstanceManager.getInstanceByServerID(dataMap.getLong("server")).get();
        CanvaCordNotification notification = (CanvaCordNotification) dataMap.get("notification");

        switch (notification.getEventType()) {
            case NEW_ASSIGNMENT -> {
                List<Assignment> newAssignments = CacheManager.getNewAssignments(instance, notification);
                if (newAssignments.isEmpty()) {
                    System.out.println("No new assignments");
                    return;
                }
                boolean success = CanvasNotifier.notifyNewAssignments(instance, notification, newAssignments);
                if (!success) throw new CanvaCordException("New Assignments notification failed");
            }
            case NEW_ANNOUNCEMENT -> {
                List<Announcement> newAnnouncements = CacheManager.getNewAnnouncements(instance, notification);
                if (newAnnouncements.isEmpty()) {
                    System.out.println("No new announcements");
                    return;
                }
                boolean success = CanvasNotifier.notifyNewAnnouncements(instance, notification, newAnnouncements);
                if (!success) throw new CanvaCordException("New Announcements notification failed");
            }
            case ASSIGNMENT_DUE_DATE_CHANGED -> {
                List<Pair<Assignment, Pair<Date, Date>>> assignmentsWithChangedDueDates = AssignmentFilter.getAssignmentsWithChangedDueDates(instance);
                if (assignmentsWithChangedDueDates.isEmpty()) {
                    System.out.println("No due dates changed");
                    return;
                }
                boolean success = CanvasNotifier.notifyDueDateChanged(instance, notification, assignmentsWithChangedDueDates);
                if (!success) throw new CanvaCordException("Changed due dates notification failed");
            }
            case ASSIGNMENT_DUE_DATE_APPROACHING -> {
                // TODO
            }
        }

        System.out.println("Sending notifications to server " + instance.getServerID());

    }

}
