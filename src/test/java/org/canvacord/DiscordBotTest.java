package org.canvacord;

import org.canvacord.discord.commands.Command;
import org.canvacord.discord.commands.CommandBuilder;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.*;
import org.canvacord.discord.DiscordBot;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class DiscordBotTest {

    public static void main(String[] args) {

        ConfigManager c = new ConfigManager();
        c.loadConfig();

        DiscordBot bot = DiscordBot.getBotInstance();
        bot.login();

        //bot.sendMessageToChannel("lmao", 1234567890);

        DiscordApi api = bot.getApi();

        Server server = api.getServers().iterator().next();

        //TEST commands
        SlashCommand command = SlashCommand.with("ping", "Checks the functionality of this command")
                .createForServer(server)
                .join();

        //if I need to delete
        //command.delete();
        //Set<SlashCommand> commands = api.getGlobalSlashCommands().join();

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            if (interaction.getFullCommandName().equals("ping")) {
                event.getInteraction().createImmediateResponder().setContent("pong").respond();
            }
        });

    }

}
