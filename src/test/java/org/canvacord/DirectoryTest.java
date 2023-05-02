package org.canvacord;

import org.canvacord.util.file.TextbookDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class DirectoryTest {
    public static void main(String[] Args) throws IOException {
        //instantiate scanner
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Instance ID");
        //Receives courseID from user input
        String instanceID = input.nextLine();
        //Checks to see if the textbook exists in /config/textbooks
        if(!TextbookDirectory.exists(instanceID)){
            System.out.println("There currently is no textbook that exists for instance: " + instanceID);
        }
        System.out.println("Please select a textbook to add to the local directory");
        //prompts the user to choose a file from a directory
        Optional<File> storedFile = TextbookDirectory.storeTextbook(instanceID);
        if (storedFile.isEmpty())
            System.err.println("Textbook store failed.");
        else
            System.out.println("There now exists: " + TextbookDirectory.getNumberOfTextbooks(instanceID) + " textbooks in instance: " + instanceID);
    }
}
