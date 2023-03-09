package org.canvacord;

import org.canvacord.persist.ConfigManager;
import org.javacord.api.entity.channel.*;
import org.canvacord.discord.DiscordBot;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class DiscordBotTest {

    public static void main(String[] args) {

        ConfigManager c = new ConfigManager();
        c.loadConfig();

        DiscordBot bot = DiscordBot.getBotInstance();
        bot.login();

        bot.sendMessageToChannel("lmao", 1234567890);

        // Potentially iterate through channel set?
        //Channel channel = channels.iterator().next();


        //Set<ChannelCategory> category = bot.getApi().getChannelCategories();

        //Server server = bot.getApi().getServerById(1016848330992656415L).get();
        //ServerTextChannel channel2 = new ServerTextChannelBuilder(server)
        //        .setName("Assignments")
        //        .create().join();

        //Choose which channel to send the message to
        //channel.sendMessage("Testing - Cisco, lmao");

    }

}
