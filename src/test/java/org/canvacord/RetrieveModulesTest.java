package org.canvacord;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AccountReader;
import edu.ksu.canvas.model.Account;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import edu.ksu.canvas.oauth.OauthToken;
import org.canvacord.canvas.CanvasApi;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.model.Module;
import edu.ksu.canvas.interfaces.ModuleReader;
import edu.ksu.canvas.requestOptions.ListModulesOptions;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;


import org.canvacord.util.input.UserInput;

import java.io.IOException;
import java.net.URL;

import org.canvacord.util.net.RemoteFileGetter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;


public class RetrieveModulesTest {

    public static void main(String[] args) throws IOException {

        String canvasBaseUrl = "https://csulb.instructure.com/";

        // Get Path of Canvas Token
        Path fileName = Path.of("config/Canvas_Token.txt");

        // read the file
        String token = Files.readString(fileName);

        // Use CSULB url and token to make CanvasAPI object
        CanvasApi canvasApi = new CanvasApi(canvasBaseUrl, token);

        // Print Module Urls with course ID
        List<Module> modules = canvasApi.getModules(32202L);
        for(int i = 0; i < modules.size(); i++) {
            System.out.println(modules.get(i).getItemsUrl());
        }

        // Test to request information from Canvas Module URL

        // For simplification
        String url = modules.get(0).getItemsUrl().toString();

        // Create URL Object
        URL obj = new URL(url);

        // Create HttpURLConnection Object
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Set RequestMethod and Request Property
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Use token for authorization
        con.setRequestProperty("Authorization", "Bearer "+ token);

        // Get Response to verify whether authentication was successful
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        // Read information from URL with BufferedReader Object
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print as a string
        System.out.println(response.toString());

        // Put url in JSON Array Object
        JSONArray jsonArr = new JSONArray(response.toString());

        // Initialize JSON Object
        for (int i = 0; i < jsonArr.length(); i++)
        {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            System.out.println(jsonObj);
        }

        // Print URL
        System.out.println(jsonArr.getJSONObject(0).get("url"));

        // REQUEST NUMBER 2

        // For simplification
        url = jsonArr.getJSONObject(0).get("url").toString();

        // Create URL Object
        obj = new URL(url);

        // Create HttpURLConnection Object
        con = (HttpURLConnection) obj.openConnection();

        // Set RequestMethod and Request Property
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Use token for authorization
        con.setRequestProperty("Authorization", "Bearer "+ token);

        // Get Response to verify whether authentication was successful
        responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + jsonArr.getJSONObject(0).get("url"));
        System.out.println("Response Code : " + responseCode);

        // Read information from URL with BufferedReader Object
        in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine2;
        response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print as a string
        System.out.println(response.toString());

        JSONObject json = new JSONObject(response.toString());

        // Print Download URL
        System.out.println(json.get("url"));

        //DOWNLOAD REQUEST!!!!

        // Download URL, contains the file
        url = json.get("url").toString();

        // Print name of file
        System.out.println(json.get("display_name").toString());

        // Create URL Object, redundant (will clean up later) just for simplification
        obj = new URL(url);

        // Create HttpURLConnection Object
        con = (HttpURLConnection) obj.openConnection();

        // Set RequestMethod and Request Property
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Use token for authorization
        con.setRequestProperty("Authorization", "Bearer "+ token);

        // Get Response to verify whether authentication was successful
        responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        //VERIFIED? NOW TRY SAVING FILE

        // Requesting input data from server
        // con.getInputStream();

        // Initialize inputStream
        InputStream inputStream = null;

        // Initialize OutputStream
        OutputStream outputStream = null;

        // Where to save data?
        outputStream = new FileOutputStream("C://Users/frive/Documents/CanvaCord/config/" + json.get("display_name").toString());

        //Getting content Length
        int contentLength = con.getContentLength();
        System.out.println("File contentLength = " + contentLength + " bytes");

        // Requesting input data from server
        inputStream = con.getInputStream();

        // Limiting byte written to file per loop
        byte[] buffer = new byte[2048];

        // Increments file size
        int length;
        int downloaded = 0;

        // Looping until server finishes
        while ((length = inputStream.read(buffer)) != -1)
        {
            // Writing data
            outputStream.write(buffer, 0, length);
            downloaded+=length;
            //System.out.println("Download Status: " + (downloaded * 100) / (contentLength * 1.0) + "%");
        }

        // Close both streams
        outputStream.close();
        inputStream.close();
    }
}
