package org.canvacord;
import org.canvacord.util.file.TextbookDirectory;
import java.io.*;
import java.util.Scanner;

public class DirectoryTest {
    public static void main(String[] Args) throws IOException {
        //instantiate scanner
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Course ID");
        //Receives courseID from user input
        String courseID = input.nextLine();
        //Checks to see if the textbook exists in /config/textbooks
        if(!TextbookDirectory.exists(courseID)){
            System.out.println("There currently is no textbook that exists for course: " + courseID);
        }
        System.out.println("Please select a textbook to add to the local directory");
        //prompts the user to choose a file from a directory
        TextbookDirectory.chooseTextbook(courseID);
        System.out.println("There now exists: " + TextbookDirectory.getNumberOfTextbooks(courseID) + " textbooks in course: " + courseID);
    }
}
