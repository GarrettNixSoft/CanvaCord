package org.canvacord;

import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.persist.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public class RetrieveAssignmentTest {

    public static void main(String[] args) throws IOException {

        ConfigManager.loadConfig();

        System.out.println("Assignment Test");

        // Get Path of Canvas Token
        Path fileName = Path.of("config/Canvas_Token.txt");

        // Now calling Files.readString() method to
        // Throw string into sting token
        String token = Files.readString(fileName);

        // Use CSULB url and student token to access canvas api
        CanvasApi canvasApi = CanvasApi.getInstance();

        // Retrieve assignment list from Canvas API
        List<Assignment> assignments = canvasApi.getAssignments("32202");
        for(int i = 0; i < assignments.size(); i++) {
            System.out.println(assignments.get(i).getName());
        }
        // For testing psuedocode
        // now that we have the list of assignments
        // for loop through assignments
        Date startDate = new Date(123, 02, 5);
        Date endDate = new Date(123, 02, 18);

        assignments = canvasApi.getAssignmentsByDateRange("32202", startDate, endDate);
        // return assignments
        for(int i = 0; i < assignments.size(); i++) {
            System.out.println(assignments.get(i).getName());
        }

    }
}
