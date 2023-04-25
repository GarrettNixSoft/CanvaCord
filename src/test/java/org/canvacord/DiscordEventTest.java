package org.canvacord;

import edu.ksu.canvas.model.Conversation;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.model.assignment.Quiz;
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
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class DiscordEventTest {
    public static void main(String[] args) throws IOException, InterruptedException
    {
        //Start Discord bot
        ConfigManager.loadConfig();
        DiscordBot bot = DiscordBot.getBotInstance();
        bot.login();

        DiscordApi api = bot.getApi();

        String token = ConfigManager.getCanvasToken();

        // Use CSULB url and student token to access canvas api
        CanvasApi canvasApi = CanvasApi.getInstance();

        //Instantiate variables
        List<Quiz> quizzes = canvasApi.getQuizzes("32150"); //Using CECS 475 for this example
        List<String> quizTitles = new ArrayList<String>();
        String combinedQuizzes = "";
        Set<TextChannel> channels = api.getTextChannelsByName("quizzes");
        ArrayList<TextChannel> textChannels = new ArrayList<TextChannel>(channels);

        //Gets a set of messages from the "quizzes" channel
        MessageSet messages = textChannels.get(0).getMessages(100).join();
        ArrayList<Message> messageList = new ArrayList<Message>(messages);

        //Separates the message in the quizzes channel to differentiate each quiz/exam
        for(Message message : messageList)
        {
            String[] temp = message.getContent().split("\n");
            quizTitles.addAll(Arrays.asList(temp));
        }

        //checks if quiz exists in the channel already before posting
        for(Quiz quiz : quizzes)
        {
            if(!quizTitles.contains(quiz.getTitle() + " - Due at: " + quiz.getDueAt())) {
                //quizTitles.add(quiz.getTitle() + "- Due at: " + quiz.getDueAt());
                combinedQuizzes += quiz.getTitle() + " - Due at: " + quiz.getDueAt() + "\n";
            }
        }
        //Sends message to the channel
        bot.sendMessageToChannel(combinedQuizzes, textChannels.get(0).getId());

        //Waits before disconnecting the bot
        Thread.sleep(100);
        bot.disconnect();
    }
}
