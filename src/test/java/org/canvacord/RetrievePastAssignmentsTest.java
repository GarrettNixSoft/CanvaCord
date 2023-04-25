package org.canvacord;

import edu.ksu.canvas.model.Conversation;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RetrievePastAssignmentsTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        ConfigManager.loadConfig();
        DiscordBot bot = DiscordBot.getBotInstance();
        bot.login();

        DiscordApi api = bot.getApi();

        System.out.println("Assignment Test");

        // Get Path of Canvas Token
        //Path fileName = Path.of("config/Canvas_Token.txt");

        // Now calling Files.readString() method to
        // Throw string into sting token
        //String token = Files.readString(fileName);
        String token = ConfigManager.getCanvasToken();

        // Use CSULB url and student token to access canvas api
        CanvasApi canvasApi = CanvasApi.getInstance();

        // Retrieve assignment list from Canvas API
        List<Assignment> assignments = canvasApi.getAssignmentsOptions("32202", "", ListCourseAssignmentsOptions.Bucket.PAST);
        Set<TextChannel> channels = api.getTextChannelsByName("past-assignments");
        ArrayList<TextChannel> textChannels = new ArrayList<TextChannel>(channels);
        if(!textChannels.isEmpty())
            //bot.sendMessageToChannel("hi", (long) textChannels.get(0).getId());
        //bot.sendMessageToChannel("hi", 1100235269334110278L);
        System.out.println(textChannels.get(0).getId());
        MessageSet messages = textChannels.get(0).getMessages(100).join();
        ArrayList<Message> messageList = new ArrayList<Message>(messages);
        ArrayList<String> messageContentList = new ArrayList<String>();
        for(Message message : messageList)
        {
            String[] temp = message.getContent().split("\n");
            messageContentList.addAll(Arrays.asList(temp));
        }
//        System.out.println("This statement is: " + messageContentList.contains("this is a message"));
//        System.out.println("message");
        /*for(Message message : messageList) {
            System.out.println(message.getContent());
        }*/
        String combinedString = "";
        for (Assignment assignment : assignments) {
            if(!messageContentList.contains(assignment.getName())) {
                //bot.sendMessageToChannel(assignment.getName(), (long) textChannels.get(0).getId());
                //Thread.sleep(1000);
                combinedString += assignment.getName() + "\n";
            }
        }
        bot.sendMessageToChannel(combinedString, (long) textChannels.get(0).getId());
        Thread.sleep(100);
        bot.disconnect();
    }
}
