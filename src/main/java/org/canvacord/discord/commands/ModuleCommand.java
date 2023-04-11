package org.canvacord.discord.commands;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.json.JSONArray;
import org.javacord.api.entity.message.component.Button;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ModuleCommand extends Command implements ButtonClickListener {
    private int assignmentsPerPage = 3; //arbitrary number
    private int currentPage;
    private Instance instance;
    JSONArray fetchedModules = new JSONArray();


    @Override
    public String getDescription() {
        return "see course module list for specified class";
    }

    @Override
    public String getShortDescription() {
        return "see course module list";
    }

    @Override
    public String getName() {
        return "modulelist";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        // get instance, server,
        Instance instanceForCourse = InstanceManager.getInstanceByServerID(interaction.getServer().get().getId()).get();
        Server server = interaction.getServer().orElseThrow(CanvaCordException::new);
        //instanceForCourse = InstanceManager.getInstanceByServerID(server.getId()).orElseThrow(CanvaCordException::new);
        //String courseID = instanceForCourse.getCourseID();
        //CompletableFuture<InteractionOriginalResponseUpdater> response;
        DiscordApi api = interaction.getApi();
        interaction.respondLater(true).thenAccept(interactionOriginalResponseUpdater -> {

            // might need to change this
            ConfigManager c = new ConfigManager();
            c.loadConfig();

            //long course = Long.parseLong(32109L);
            CanvasApi canvasApi = new CanvasApi(c.getCanvasURL(), c.getCanvasToken());
            try {
                fetchedModules = canvasApi.getAllModules(Long.parseLong(instanceForCourse.getCourseID()), c.getCanvasToken());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // get the minimum between the default value or the num of fetched assignments
            assignmentsPerPage = Math.min(assignmentsPerPage,fetchedModules.length());
            for (EmbedBuilder embed : assignmentEmbeds().subList(0, assignmentsPerPage)) {
                interactionOriginalResponseUpdater.addEmbed(embed).update();
            }

            if (fetchedModules.length()>assignmentsPerPage) {
                interactionOriginalResponseUpdater
                        .addComponents(ActionRow.of(Button.danger("forward", "▶")))
                        .update().thenAccept(originalResponse -> {
                            api.addButtonClickListener(this).removeAfter(10, TimeUnit.MINUTES);
                        });
            }

        });
    }
    private List<EmbedBuilder> assignmentEmbeds(){
        List <EmbedBuilder> assignmentEmbeds = new ArrayList<>();
        for (int i=0; i < fetchedModules.length(); i++) {
            System.out.println(fetchedModules.getJSONObject(i).get("title").toString());
            EmbedBuilder assignmentEmbed = new EmbedBuilder()
                    .setTitle(fetchedModules.getJSONObject(i).get("title").toString())
                    .setUrl(fetchedModules.getJSONObject(i).get("url").toString())
                    .setColor(Color.RED);
            /*]]
            if (assignment.getDescription()!= null){
                assignmentEmbed.setDescription(assignment.getDescription());
            }
            else{
                assignmentEmbed.setDescription(assignment.getName());
            }
            if (assignment.getDueAt() != null){
                assignmentEmbed.addField("Due",assignment.getDueAt().toString());
            }
            */
            assignmentEmbeds.add(assignmentEmbed);
        }
        return assignmentEmbeds;
    }


    @Override
    public SlashCommandBuilder getBuilder(Instance instance) {
        return SlashCommand.with(getName(),getShortDescription());
    }

    @Override
    public void onButtonClick(ButtonClickEvent buttonClickEvent) {
        ButtonInteraction interaction = buttonClickEvent.getButtonInteraction();
        ComponentInteractionOriginalMessageUpdater response = interaction.createOriginalMessageUpdater();
        String buttonID = interaction.getCustomId();
        List<LowLevelComponent> buttons = new ArrayList<>();

        int pageStart = currentPage * assignmentsPerPage;
        int pageEnd;

        if (buttonID.equals("forward") && fetchedModules.length()-(pageStart+assignmentsPerPage)>0){
            currentPage+=1;
            buttons.add(Button.danger("back","◀"));

            if(fetchedModules.length()-((currentPage * assignmentsPerPage)+assignmentsPerPage)>0){ //ugly yo
                buttons.add(Button.danger("forward", "▶"));
            }
        }
        else if (buttonID.equals("back") && currentPage!=0) {
            currentPage -= 1;

            if(currentPage != 0){
                buttons.add(Button.danger("back","◀"));
            }

            // put after the if statement to preserve button order
            buttons.add(Button.danger("forward", "▶"));
        }

        pageStart = currentPage * assignmentsPerPage;
        pageEnd = Math.min(pageStart + assignmentsPerPage, fetchedModules.length());


        response.removeAllEmbeds();
        for (EmbedBuilder embed : assignmentEmbeds().subList(pageStart, pageEnd)) {
            response.addEmbed(embed);
        }

        response.addComponents(ActionRow.of(buttons)).update();
    }
}
