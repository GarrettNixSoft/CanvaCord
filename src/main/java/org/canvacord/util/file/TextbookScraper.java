package org.canvacord.util.file;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class TextbookScraper {
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

        //System.out.println("Search results for: " + bookName);
        bookRows.remove(0);
        bookRows.remove(0);
        bookRows.remove(0);
        return bookRows.first().select("td").get(9).select("a").attr("href");
    }
}
