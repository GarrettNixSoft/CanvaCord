package org.canvacord;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONArray;

import java.io.IOException;

public class DiscordBotTest {

    public static void main(String[] args) throws IOException {

        ConfigManager.loadConfig();

        DiscordBot bot = DiscordBot.getBotInstance();
        bot.login();

        //bot.sendMessageToChannel("lmao", 1234567890);

        CanvasApi canvasApi = CanvasApi.getInstance();

        DiscordApi api = bot.getApi();

        //First and only server
        Server server = api.getServers().iterator().next();

        //String that will be displayed
        String[] stringArray = new String[5];
        String moduleStrings = "";

        JSONArray modules = canvasApi.getAllModules(32109L, ConfigManager.getCanvasToken());
        //JSONArray modulesInfo = canvasApi.getModuleInfo(32109L, c.getCanvasToken());

        int j = 0;

        for (int i = 0; i < modules.length(); i++) {

            if(i == 0) {
                //skip
            }
            else if(i%10 == 0) {
                //add to string array
                stringArray[j] = moduleStrings;
                //increment j
                j++;
                //clear module string
                moduleStrings = "";
            }
            else if(i < 10 && i == modules.length()-1) {
                stringArray[j] = moduleStrings;
            }


            /*
            moduleStrings += modulesInfo.getJSONObject(i).get("title").toString() + "\n";

            if (modulesInfo.getJSONObject(i).get("type").toString().equals("File")){
                moduleStrings += modules.getJSONObject(i).get("url").toString() + "\n";
            }
            else if(modulesInfo.getJSONObject(i).get("type").toString().equals("Page")){
                moduleStrings += modules.getJSONObject(i).get("html_url").toString() + "\n";
            }
            else {
                moduleStrings += modules.getJSONObject(i).get("external_url").toString() + "\n";
            }

            if (modules.getJSONObject(i).has("display_name")) {
                moduleStrings += modules.getJSONObject(i).get("url").toString() + "\n";
            }
            else if (modules.getJSONObject(i).has("title")){

            }

            *
             */
            /*
            if (modulesInfo.getJSONObject(i).get("type").toString().equals("File")){
                moduleStrings += modules.getJSONObject(i).get("url").toString() + "\n";
            }
            else if(modulesInfo.getJSONObject(i).get("type").toString().equals("Page")){
                moduleStrings += modules.getJSONObject(i).get("html_url").toString() + "\n";
            }
            else {
                moduleStrings += modules.getJSONObject(i).get("external_url").toString() + "\n";
            }

             */
            //+ modules.getJSONObject(i).get("url").toString() + "\n";
        }

        //TEST commands
        SlashCommand command = SlashCommand.with("modulelist", "Return module url")
                .createForServer(server)
                .join();

        //if I need to delete
        command.delete();

        String finalModuleStrings = moduleStrings;
        String finalModuleStrings1 = moduleStrings;
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            if (interaction.getFullCommandName().equals("modulelist")) {
                event.getInteraction()
                        .respondLater()
                        .thenAccept(interactionOriginalResponseUpdater -> {
                            interactionOriginalResponseUpdater.setContent(
                                    stringArray[0]).update();
                        });

                if (stringArray[1] != null) {
                    for (int i = 1; i < stringArray.length; i++) {
                        interaction.getChannel().get().sendMessage(stringArray[i]);
                    }
                }





            }
        });
    }
}
/*
event.getInteraction()
        .createImmediateResponder()
        .setContent(test.getJSONObject(0).get("url").toString())
        .respond();

 */
