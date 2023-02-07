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
import java.util.List;

public class RetrieveAssignmentTest {

    public static void main(String[] args) throws IOException {

        System.out.println("Assignment Test");
        String canvasBaseUrl = "https://csulb.instructure.com/";
        String token  = "21139~NV9KQAAFTDNnebJe8UEbigucgfS08OlofVtLl5r1nnhRhUevKU1jCPdXKiNp5WsY";
        CanvasApi canvasApi = new CanvasApi(canvasBaseUrl, token);
        List<Assignment> assignments = canvasApi.getAssignments("32202");
        for(int i = 0; i < assignments.size(); i++) {
            System.out.println(assignments.get(i).getName());
        }
    }
}
