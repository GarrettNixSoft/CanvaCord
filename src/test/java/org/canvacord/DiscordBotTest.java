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

        bot.sendMessageToChannel("lmao", 1083512434653921352L);
        
    }

}
