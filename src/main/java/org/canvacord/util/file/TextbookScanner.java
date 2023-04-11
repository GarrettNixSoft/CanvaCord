package org.canvacord.util.file;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
public class TextbookScanner {
    public static String getTextbookName(){
        String searchWord = "book";
        String filename = "example.pdf";
        File test = TextbookDirectory.chooseTextbook().get();
        String text;

        try (PDDocument document = PDDocument.load(test)) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        String[] lines = text.split("\\r?\\n");
        String textbookLine = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(searchWord)) {
                if (i < lines.length - 1) {
                    System.out.println(lines[i]);
                    textbookLine = lines[i];
                }
                break;
            }
        }
        String[] textbook = textbookLine.split(":");
        System.out.println(textbook[1].trim());
        return textbook[1].trim();
    }
}
