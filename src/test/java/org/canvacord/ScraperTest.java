package org.canvacord;

import java.io.IOException;
import java.util.Scanner;

import org.canvacord.util.file.TextbookScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScraperTest {

    public static void main(String[] args) throws IOException {
        /* Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a keyword to search for: ");
        String keyword = scanner.nextLine();

        String searchUrl = "https://libgen.is/search.php?req=" + keyword + "&open=0&res=25&view=simple&phrase=1&column=def";

        Document doc = Jsoup.connect(searchUrl).get();
        Elements bookRows = doc.select("table > tbody > tr");

        System.out.println("Search results for: " + keyword);
        /*for (Element bookRow : bookRows) {
            System.out.println(bookRow.select("td").size());
            String title = bookRow.select("td").get(1).text();
            //String author = bookRow.select("td").get(2).text();
            //String downloadLink = bookRow.select("td").get(9).select("a").attr("href");
            System.out.println("Title: " + title);
            //System.out.println("Author: " + author);
            //System.out.println("Download link: " + downloadLink);
            System.out.println();
            //break;
        }
        bookRows.remove(0);
        bookRows.remove(0);
        bookRows.remove(0);
        System.out.println(bookRows.first().select("td").size());
        System.out.println(bookRows.first().select("td").get(1).text());
        System.out.println(bookRows.first().select("td").get(9).select("a").attr("href")); */
        //String url = TextbookScraper.getTextbookURL("calculus");
        //System.out.println(url);
        TextbookScraper.downloadTextbook("32202", "calculus");
    }
}