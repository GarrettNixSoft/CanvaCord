package org.canvacord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.canvacord.discord.DiscordBot;

public class DiscordBotTest {

    public static void main(String[] args) {

        DiscordBot bot = DiscordBot.getBotInstance();
        bot.login();

        




    }

}
