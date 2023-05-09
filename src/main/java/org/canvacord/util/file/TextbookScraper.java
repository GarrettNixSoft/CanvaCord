package org.canvacord.util.file;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
public class TextbookScraper {
    /**
     * Gets the Textbook Download URl
     * @param bookName
     * @return URL to download the textbook
     */
    public static String getTextbookURL(String bookName){
        //instantiating vars
        Document doc = new Document("Test");

        String searchUrl = "https://libgen.is/search.php?req=" + bookName + "&open=0&res=25&view=simple&phrase=1&column=def";

        //Opens libgen url
        try {
            doc = Jsoup.connect(searchUrl).get();
        } catch (IOException E)
        {
               System.out.println("Url does not exist or is not supported");
        }
        //Selects tr element of the webpage
        Elements bookRows = doc.select("table > tbody > tr");

        //Checks to see if there are any results

        bookRows.remove(0);
        bookRows.remove(0);
        bookRows.remove(0);
        if(bookRows.first().select("td").size() < 9)
        {
            System.out.println("No Textbook Found");
            return null;
        }
        return bookRows.first().select("td").get(9).select("a").attr("href");
    }

    /**
     * Downloads a textbook from Libgen directory given a book name
     * @param bookName
     */
    public static File downloadTextbook(String instanceID, String bookName) {
        //Textbook URL Retrieval section of code
        String searchUrl = TextbookScraper.getTextbookURL(bookName);
        Document doc = new Document("Test");
        //Opens libgen url
        try {
            doc = Jsoup.connect(searchUrl).get();
        } catch (IOException E)
        {
            System.out.println("Url does not exist or is not supported");
        }
        Elements downloads = doc.select("table > tbody > tr");

        //Testing Lines
        //System.out.println(downloads.size());
        //System.out.println(downloads.first().select("td").select("div#download").select("h2").get(0).select("a").attr("href"));

        //Download Section of Code
        String downloadURL = downloads.first().select("h2").get(0).select("a").attr("href");
        String[] parts = downloadURL.split("\\.");
        //System.out.println(parts[parts.length - 1]);

        //Try catch to see if the url is valid
        int count = 1;
        int maxLoop = 10;
        while(true)
        {
            try {
                System.out.printf("Downloading Attempt #%d\n", count);
                //Instantiate the Java.net package
                URL url = new URL(downloadURL);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                String id = "testing";
                int latestNumber = TextbookDirectory.getLatestNumber(instanceID);
                //Stores files in /config/textbooks numerically using the course ID
                FileOutputStream outputStream = new FileOutputStream(CanvaCordPaths.getInstanceDirPath(instanceID).toAbsolutePath() + "/textbook_" + latestNumber + "." + parts[parts.length - 1]);
                //Number of Bytes downloaded for each loop
                byte[] buffer = new byte[1024];
                int bytesRead;
                System.out.println("Downloading File");
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("File Finished Downloading");
                outputStream.close();
                inputStream.close();
                return new File(CanvaCordPaths.getInstanceDirPath(instanceID).toAbsolutePath() + "/textbook_" + latestNumber + "." + parts[parts.length - 1]);
            } catch (Exception E)
            {
                if(++count == maxLoop) {
                    System.out.println("Failed to Download");
                    E.printStackTrace();
                }
            }
        }

    }
}
