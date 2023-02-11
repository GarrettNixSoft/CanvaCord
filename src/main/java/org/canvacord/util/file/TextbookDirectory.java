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
    public static int latestNumber(String id) {
        int counter = 1;
        File outputFile = new File("./config/textbooks/" + id + "_" + counter + ".pdf");
        while(outputFile.exists())
        {
            outputFile = new File("./config/textbooks/" + id + "_" + ++counter + ".pdf");
        }
        return counter;
    }

    /**
     * Prompts User to Select a PDF file and stores it in the /config/textbooks folder.
     * @param id
     * @return testing
     */
    public static Optional<File> chooseTextbook(String id){
        Optional<File> testing = FileGetter.getFile("C:", "PDF File", ".pdf");
        //Checks if user closes the prompt window
        if(testing.isEmpty()) {
            System.out.println("Unable to obtain file");
            return Optional.empty();
        }
        File inputFile = testing.get();
        File outputFile = new File("./config/textbooks/" + id + "_" + latestNumber(id) + ".pdf");
        //copies inputFile to outputFile
        try {
            Files.copy(inputFile.toPath(), outputFile.toPath(), REPLACE_EXISTING);
        } catch(Exception e) {
            System.out.println("Failed to copy to path");
        }
        return testing;
    }
}
