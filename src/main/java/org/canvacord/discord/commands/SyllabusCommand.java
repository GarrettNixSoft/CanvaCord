package org.canvacord.discord.commands;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.canvas.SyllabusInfo;
import org.canvacord.discord.DiscordBot;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SyllabusCommand extends Command{
    private static final EmbedBuilder errorMessage = new EmbedBuilder().setTitle("Unable to fetch Syllabus")
            .setDescription("We're sorry, but the requested file is not available. Contact the bot owner or consult the user manual for setup help.");
    @Override
    public String getDescription(){
        return """
                This command fetches a Canvas course's Syllabus file, and if it fails in doing that, the CanvaCord bot instance owner can provide one manually.
                Call me using: */syllabus* !
                
                ***"Why isn't a file appearing..?"***
                - *The CanvaCord bot instance owner has not performed the Syllabus fetch.*
                - *The file may have been moved or altered.*
                """;
    }
    @Override
    public String getShortDescription(){
        return "Fetch the syllabus file for a course.";
    }
    @Override
    public String getName(){
        return "syllabus";
    }
    @Override
    public SlashCommandBuilder getBuilder(Instance instance){
        return SlashCommand.with(getName(),getShortDescription());
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {

        CompletableFuture<InteractionOriginalResponseUpdater> response;
        // Use respondLater to send a response that takes longer than 3 seconds
        // set the value to true to make it ephemeral
        response = interaction.respondLater(true);

        response.thenAccept(interactionOriginalResponseUpdater -> {
            Server server = interaction.getServer().orElseThrow(()->new CanvaCordException("Server not found"));
            Instance instanceForCourse = InstanceManager.getInstanceByServerID(server.getId()).orElseThrow(()->new CanvaCordException("Instance not found"));



            if(instanceForCourse.hasSyllabus()){
                Optional<SyllabusInfo> syllabus = CanvasApi.getInstance().findSyllabusComplete(instanceForCourse.getCourseID());

                syllabus.ifPresentOrElse(syllabusData -> {
                        interactionOriginalResponseUpdater
                            .addEmbed(new EmbedBuilder().setTitle(instanceForCourse.getCourseTitle() + " Syllabus")
                                .setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
                                .setColor(Color.RED)
                                .addField("Course ",instanceForCourse.getCourseTitle())
                                .addInlineField("Size ",syllabusData.getFileSize())
                                .addInlineField("Last modified",syllabusData.getLastModified().toString()))
                            .addAttachment(syllabusData.getSyllabusFile()).update();
                }, () -> interactionOriginalResponseUpdater.addEmbed(errorMessage).update());
            }
            else {
                interactionOriginalResponseUpdater.addEmbed(errorMessage).update();
            }
        });
    }
}
