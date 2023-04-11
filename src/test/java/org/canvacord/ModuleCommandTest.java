package org.canvacord;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
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

        //First and only server
        Server server = api.getServers().iterator().next();

        ModuleCommand command = new ModuleCommand();

        // Create the command in the target server
        command.getBuilder()
                .createForServer(server)
                .join();
        //if I need to delete
        //command.delete();

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            if (interaction.getFullCommandName().toLowerCase().contains("modulelist"))
                command.execute(event.getSlashCommandInteraction());
        });
    }
}
