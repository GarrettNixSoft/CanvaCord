package org.canvacord;

import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RetrievePastAssignmentsTest {
    public static void main(String[] args) throws IOException {
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
        TextChannel testing = api.getTextChannelById(1100235269334110278L).get();
        //bot.sendMessageToChannel("lmao", 1100235269334110278L);
        Set<ServerTextChannel> channels = api.getServerTextChannelsByName("past-assignments");
        ArrayList<ServerTextChannel> textChannels = new ArrayList<ServerTextChannel>(channels);
        if(!textChannels.isEmpty())
            bot.sendMessageToChannel("Hi!", (long) textChannels.get(0).getId());
        System.out.println(textChannels.get(0).getId());
        for(int i = 0; i < assignments.size(); i++) {
            System.out.println(assignments.get(i).getName());
        }
        bot.disconnect();
    }
}
