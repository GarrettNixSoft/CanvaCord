package org.canvacord;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AccountReader;
import edu.ksu.canvas.model.Account;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import edu.ksu.canvas.oauth.OauthToken;
import org.canvacord.canvas.CanvasApi;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.model.Module;
import edu.ksu.canvas.interfaces.ModuleReader;
import edu.ksu.canvas.requestOptions.ListModulesOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.net.URL;
import java.util.Scanner;

public class RetrieveModulesTest {

    public static void main(String[] args) throws IOException {

        System.out.println("Module Test");
        String canvasBaseUrl = "https://csulb.instructure.com/";

        // Get Path of Canvas Token
        Path fileName = Path.of("config/Canvas_Token.txt");

        // Now calling Files.readString() method to
        // read the file
        String token = Files.readString(fileName);

        //throw token in CanvasApi
        CanvasApi canvasApi = new CanvasApi(canvasBaseUrl, token);
        List<Module> modules = canvasApi.getModules(32202L);
        for(int i = 0; i < modules.size(); i++) {
            System.out.println(modules.get(i).getItemsUrl());
        }
    }
}
