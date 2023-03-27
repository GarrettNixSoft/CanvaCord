package org.canvacord.util.file;
import java.io.*;
import java.util.Optional;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class TextbookDirectory {
    /**
     * Checks for existings files and finds vacant number
     * @param id
     * @return counter
     */
    public static int getLatestNumber(String id) {
        int counter = 1;
        File outputFile = new File("./config/textbooks/" + id + "_" + counter + ".pdf");
        while(outputFile.exists())
        {
            outputFile = new File("./config/textbooks/" + id + "_" + ++counter + ".pdf");
        }
        return counter;
    }

    /**
     * Shows how many textbooks are in a given course
     * @param id course id
     * @return the number of textbooks in a given course
     */
    public static int getNumberOfTextbooks(String id){
        if(!exists(id)){
            return 0;
        }
        return getLatestNumber(id) - 1;
    }

    /**
     * Prompts User to Select a PDF file and stores it in the /config/textbooks folder.
     * @param id The course id of the course too add the textbook file too
     * @return the chosen pdf file that the user chosen
     */
    public static Optional<File> storeTextbook(String id){
        Optional<File> testing = FileGetter.getFile(System.getProperty("user.dir"), "PDF File", ".pdf");
        //Checks if user closes the prompt window
        if(testing.isEmpty()) {
            System.out.println("Unable to obtain file");
            return Optional.empty();
        }
        File inputFile = testing.get();
        File outputFile = new File("./config/textbooks/" + id + "_" + getLatestNumber(id) + ".pdf");
        //copies inputFile to outputFile
        try {
            Files.copy(inputFile.toPath(), outputFile.toPath(), REPLACE_EXISTING);
        } catch(Exception e) {
            System.out.println("Failed to copy to path");
        }
        return testing;
    }

    public static Optional<File> storeTextbook(String id, File inputFile){
        File outputFile = new File("./config/textbooks/" + id + "_" + getLatestNumber(id) + ".pdf");
        //copies inputFile to outputFile
        try {
            Files.copy(inputFile.toPath(), outputFile.toPath(), REPLACE_EXISTING);
            return Optional.of(outputFile);
        } catch(Exception e) {
            System.out.println("Failed to copy to path");
            return Optional.empty();
        }
    }

    public static Optional<File> chooseTextbook(){
        Optional<File> testing = FileGetter.getFile("C:", "PDF File", ".pdf");
        //Checks if user closes the prompt window
        if(testing.isEmpty()) {
            System.out.println("Unable to obtain file");
            return Optional.empty();
        }
        return testing;
    }

    /**
     * Checks to see if a course already has a pdf associated with it in the /config/textbooks folder
     * @param id course id
     * @return a boolean of whether the file exists or not
     */
    public static boolean exists(String id){
        File outputFile = new File("./config/textbooks/" + id + "_1.pdf");
        return outputFile.exists();
    }


}
