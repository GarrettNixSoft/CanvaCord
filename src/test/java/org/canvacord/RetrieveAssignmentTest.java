package org.canvacord;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AccountReader;
import edu.ksu.canvas.model.Account;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import edu.ksu.canvas.oauth.OauthToken;
import org.canvacord.canvas.CanvasApi;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.model.assignment.Assignment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RetrieveAssignmentTest {

    public static void main(String[] args) throws IOException {


        System.out.println("Assignment Test");
        String canvasBaseUrl = "https://csulb.instructure.com/";

        // Get Path of Canvas Token
        Path fileName = Path.of("config/Canvas_Token.txt");

        // Now calling Files.readString() method to
        // Throw string into sting token
        String token = Files.readString(fileName);

        // Use CSULB url and student token to access canvas api
        CanvasApi canvasApi = new CanvasApi(canvasBaseUrl, token);

        // Retrieve assignment list from Canvas API
        List<Assignment> assignments = canvasApi.getAssignments("32202");
        for(int i = 0; i < assignments.size(); i++) {
            System.out.println(assignments.get(i).getName());
        }
    }
}
