package org.canvacord.discord.commands;

import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.canvas.TextbookFetcher;
import org.canvacord.canvas.TextbookInfo;
import org.canvacord.discord.DiscordBot;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.Globals;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TextbookFinderCommand extends Command {

    private static final EmbedBuilder errorMessage = new EmbedBuilder().setTitle("Unable to fetch Textbook")
            .setDescription("We're sorry, but the requested file is not available. Contact the bot owner or consult the user manual for setup help.");
    @Override
    public String getDescription(){
        return """
                This command fetches a Canvas course's Textbook file, and if it fails in doing that, the CanvaCord bot instance owner can provide one manually.
                Call me using: */textbook* !
                
                ***"Why isn't a file appearing..?"***
                - *The CanvaCord bot instance owner has not performed the Textbook fetch.*
                - *The file may have been moved or altered.*
                """;
    }
    @Override
    public String getShortDescription(){
        return "Fetch the textbook file for a course.";
    }
    @Override
    public String getName(){
        return "textbookfinder";
    }
    @Override
    public SlashCommandBuilder getBuilder(Instance instance){
        return SlashCommand.with(getName(),getShortDescription(), List.of(SlashCommandOption.create(
                SlashCommandOptionType.STRING,
                "textbook",
                "Enter Textbook Name",
                true
        )));
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
            Globals.EDIT_INSTANCE_ID = instanceForCourse.getInstanceID();
            Optional<TextbookInfo> textbookDummy = TextbookFetcher.fetchTextbookOnline(interaction.getOptionByIndex(0).get().getStringValue().get(), instanceForCourse.getCourseID());

            if(!instanceForCourse.getTextbooks().isEmpty()){

                textbookDummy.ifPresentOrElse(textbookData -> {
                    interactionOriginalResponseUpdater
                            .addEmbed(new EmbedBuilder().setTitle(textbookData.getTitle() + " Textbook")
                                    .setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
                                    .setColor(Color.RED)
                                    .addField("Course ",instanceForCourse.getCourseTitle()))
                            .addAttachment(textbookData.getTextbookFile()).update();
                }, () -> interactionOriginalResponseUpdater.addEmbed(errorMessage).update());
            }
            else {
                interactionOriginalResponseUpdater.addEmbed(errorMessage).update();
            }
        });
    }
}
