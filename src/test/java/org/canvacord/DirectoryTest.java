package org.canvacord;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AccountReader;
import edu.ksu.canvas.model.Account;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import edu.ksu.canvas.oauth.OauthToken;
import org.canvacord.canvas.CanvasApi;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.model.Module;
import edu.ksu.canvas.interfaces.ModuleReader;
import edu.ksu.canvas.requestOptions.ListModulesOptions;
import org.canvacord.util.file.FileGetter;
import org.canvacord.util.file.TextbookDirectory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Optional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.net.URL;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DirectoryTest {
    public static void main(String[] Args) throws IOException {
        /**
        Optional<File> testing = FileGetter.getFile("C:", "PDF File", ".pdf");
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        File inputFile = testing.get();
        File outputFile = new File("./config/textbooks/" + "bruhmoment" + ".pdf");

        while(outputFile.exists())
        {
            outputFile = new File("./config/textbooks/" + "bruhmoment" + ".pdf");
        }
        //System.out.println(outputFile.getAbsolutePath());
        //System.out.println(inputFile.toPath());

        Files.copy(inputFile.toPath(), outputFile.toPath());
        **/
        TextbookDirectory.chooseTextbook("testing");
    }
}
