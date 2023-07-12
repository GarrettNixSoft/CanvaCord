package org.canvacord;

import edu.ksu.canvas.model.Module;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.persist.ConfigManager;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


public class RetrieveModulesTest {

    public static void main(String[] args) throws IOException {

        ConfigManager.loadConfig();

        // Get Path of Canvas Token
        Path fileName = Path.of("config/Canvas_Token.txt");

        // Use CSULB url and token to make CanvasAPI object
        CanvasApi canvasApi = CanvasApi.getInstance();

        // Print Module Urls with course ID
        List<Module> modules = canvasApi.getModules("32202");
        for(int i = 0; i < modules.size(); i++) {
            System.out.println(modules.get(i).getItemsUrl());
        }

        JSONArray test = canvasApi.getDownloadableModules("32202");

        for(int i = 0; i < test.length(); i++) {
            System.out.println(test.getJSONObject(i).get("url"));
        }
        /*
        // will hold all downloadable module json objects
        JSONArray downloadableModules = new JSONArray();

        // Test to request information from Canvas Module URL
        // For simplification

        List<String> urls = new ArrayList<>();

        for(int i = 0; i < modules.size(); i++) {
            urls.add(modules.get(i).getItemsUrl().toString());
        }
        //String url = modules.get(1).getItemsUrl().toString();


        //FOR LOOP ALL THIS

        for (int i = 0; i < urls.size(); i++) {
            // Create URL Object
            URL obj = new URL(urls.get(i));

            // Create HttpURLConnection Object
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set RequestMethod and Request Property
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Use token for authorization
            con.setRequestProperty("Authorization", "Bearer " + token);

            // Get Response to verify whether authentication was successful
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + urls.get(i).toString());
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

            // Print JSON Object
            for (int j = 0; j < jsonArr.length(); j++) {
                // if jsonObj is a file throw it into the downloadableModules JSON Array
                JSONObject jsonObj = jsonArr.getJSONObject(j);
                if (jsonArr.getJSONObject(j).get("type").toString().equals("File")) {
                    System.out.println(jsonObj);
                    downloadableModules.put(jsonArr.getJSONObject(j));
                }
            }
        }

        //HOLD HERE
        // Print URL

        for(int i = 0; i < downloadableModules.length(); i++) {
            System.out.println(downloadableModules.getJSONObject(i));
        }

        // REQUEST NUMBER 2
        //downloadableModules hold all possible downloadable modules

        for(int i = 0; i < downloadableModules.length(); i++) {
            // For simplification
            String url = downloadableModules.getJSONObject(i).get("url").toString();

            // Create URL Object
            URL obj = new URL(url);

            // Create HttpURLConnection Object
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set RequestMethod and Request Property
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Use token for authorization
            con.setRequestProperty("Authorization", "Bearer " + token);

            // Get Response to verify whether authentication was successful
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + downloadableModules.getJSONObject(i).get("url").toString());
            System.out.println("Response Code : " + responseCode);

            // Read information from URL with BufferedReader Object
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine2;
            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print as a string
            System.out.println(response.toString());

            JSONObject json = new JSONObject(response.toString());
            //replace with new json object containing downloadable url
            downloadableModules.put(i, json);
            // Print JSON Object

            // Print Download URL
            System.out.println(json.get("display_name"));
            System.out.println(json.get("url"));
        }

        for(int i = 0; i < downloadableModules.length(); i++) {
            System.out.println(downloadableModules.getJSONObject(i));
        }


        /*
        //STOP HERE
        //DOWNLOAD REQUEST!!!!

        // Download URL, contains the file
        String url = json.get("url").toString();

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

         */
    }
}
