package org.canvacord;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.discord.commands.CommandHandler;
import org.canvacord.discord.commands.ModuleCommand;
import org.canvacord.discord.commands.RemindMeCommand;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONArray;

import java.io.IOException;

public class ModuleCommandTest {

    public static void main(String[] args) throws IOException {

        ConfigManager c = new ConfigManager();
        c.loadConfig();

        DiscordBot bot = DiscordBot.getBotInstance();
        bot.login();

        CanvasApi canvasApi = new CanvasApi(c.getCanvasURL(), c.getCanvasToken());

        DiscordApi api = bot.getApi();

        InstanceManager.loadInstances();
        CommandHandler.init();



        //First and only server
        Server server = api.getServerById(1016848330992656415L).get();

        CommandHandler.registerCommandServer(ModuleCommand.class, server);

//        api.addSlashCommandCreateListener(event -> {
//            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
//            if (interaction.getFullCommandName().toLowerCase().contains("modulelist"))
//                command.execute(event.getSlashCommandInteraction());
//        });
    }
}
