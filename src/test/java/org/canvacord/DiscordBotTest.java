package org.canvacord;

import org.canvacord.canvas.CanvasApi;
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
import org.json.JSONArray;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class DiscordBotTest {

    public static void main(String[] args) throws IOException {

        ConfigManager c = new ConfigManager();
        c.loadConfig();

        DiscordBot bot = DiscordBot.getBotInstance();
        bot.login();

        //bot.sendMessageToChannel("lmao", 1234567890);

        CanvasApi canvasApi = new CanvasApi(c.getCanvasURL(), c.getCanvasToken());

        DiscordApi api = bot.getApi();

        //First and only server
        Server server = api.getServers().iterator().next();

        JSONArray test = canvasApi.getDownloadableModules(32202L, c.getCanvasToken());


        //TEST commands
        SlashCommand command = SlashCommand.with("moduletest", "Return module url")
                .createForServer(server)
                .join();

        //if I need to delete
        //command.delete();
        //Set<SlashCommand> commands = api.getGlobalSlashCommands().join();

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            if (interaction.getFullCommandName().equals("moduletest")) {
                event.getInteraction().createImmediateResponder().setContent(test.getJSONObject(0).get("url").toString()).respond();
            }
        });

    }

}
