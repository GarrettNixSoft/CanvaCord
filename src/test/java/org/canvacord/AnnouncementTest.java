package org.canvacord;

import edu.ksu.canvas.model.announcement.Announcement;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.persist.ConfigManager;

import java.io.IOException;
import java.util.List;

public class AnnouncementTest {
    public static void main(String[] args) throws IOException {
        ConfigManager.loadConfig();

        System.out.println("Announcement Test");

        // Use CSULB url and student token to access canvas api
        CanvasApi canvasApi = CanvasApi.getInstance();

        // Retrieve assignment list from Canvas API
        List<Announcement> announcements = canvasApi.getAnnouncements("32202");
        for(int i = 0; i < announcements.size(); i++) {
            System.out.println("Posted at: "+ announcements.get(i).getPostedAt().toString()+", "+ announcements.get(i).getMessage());
        }

    }
}
