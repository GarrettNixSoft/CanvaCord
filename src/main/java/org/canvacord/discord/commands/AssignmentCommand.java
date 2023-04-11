package org.canvacord.discord.commands;

import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.canvas.AssignmentFetcher;
import org.canvacord.discord.DiscordBot;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class AssignmentCommand extends Command implements ButtonClickListener {
    private final int assignmentsPerPage = 3;
    private int currentPage;
    private Instance instance;
    List<Assignment> assignments = new ArrayList<>();

    @Override
    public String getDescription() {
        return """
                the
                """;
    }

    @Override
    public String getShortDescription() {
        return "see the course assignments";
    }

    @Override
    public String getName() {
        return "assignment";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {

        currentPage = 0;

        Server server = interaction.getServer().orElseThrow(() -> new CanvaCordException("Server not found"));
        instance = InstanceManager.getInstanceByServerID(server.getId()).orElseThrow(()->new CanvaCordException("Instance not found"));
        String courseID = instance.getCourseID();
        DiscordApi api = interaction.getApi();

        // Use respondLater to send a response that takes longer than 3 seconds
        // set the value to true to make it ephemeral
        interaction.respondLater(true).thenAccept(interactionOriginalResponseUpdater -> {
            String arguments = interaction.getArgumentStringValueByName("search").orElse(
                    interaction.getArgumentStringValueByName("details").orElse(null));

            try {
                // if there are arguments, fetch assignments based on a search
                // if there are not arguments, fetch active assignments
                assignments = (arguments != null) ?
                        AssignmentFetcher.fetchAssignmentsSearch(courseID, arguments) : AssignmentFetcher.fetchAssignmentsActive(courseID);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (!interaction.getFullCommandName().contains("details")){
                sendAssignmentList(interactionOriginalResponseUpdater,api);
            }
            else{
                sendAssignmentDetails(interactionOriginalResponseUpdater);
            }
        });
    }

    private void sendAssignmentDetails(InteractionOriginalResponseUpdater interactionOriginalResponseUpdater){
        Assignment assignment = assignments.get(0);
        interactionOriginalResponseUpdater.addEmbed(new EmbedBuilder()
                .setTitle(assignment.getName())
                .setUrl(assignment.getHtmlUrl())
                .setColor(Color.RED)
                .setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
                .addField("Due",
                        (assignment.getDueAt() != null) ? assignment.getDueAt().toString() : "No due date.")
                .setDescription(
                        (assignment.getDescription() != null) ? assignment.getDescription() : assignment.getName())
                .addInlineField("points possible", String.valueOf(assignment.getPointsPossible()))
                .addInlineField(" ", assignment.getSubmissionTypes().toString())
        );

    }
    private void sendAssignmentList(InteractionOriginalResponseUpdater interactionOriginalResponseUpdater, DiscordApi api){
        // get the minimum between the default value or the num of fetched assignments
        int pageEnd = Math.min(assignmentsPerPage,assignments.size());
        for (EmbedBuilder embed : assignmentEmbeds().subList(0, pageEnd)) {
            interactionOriginalResponseUpdater.addEmbed(embed).update();
        }

        interactionOriginalResponseUpdater.addEmbed(new EmbedBuilder().setFooter("Page "+(currentPage+1) +" ("+pageEnd+"/"+assignments.size()+") results"));

        if (assignments.size()>assignmentsPerPage){
            interactionOriginalResponseUpdater
                    .addComponents(ActionRow.of(Button.danger("forward", "▶")))
                    .update().thenAccept(originalResponse -> {
                        api.addButtonClickListener(this).removeAfter(10, TimeUnit.MINUTES); //FIXME FIXME FIXME FIXME
                    });
        }
    }

    private List<EmbedBuilder> assignmentEmbeds() {

        List<EmbedBuilder> assignmentEmbeds = new ArrayList<>();

        for (Assignment assignment : assignments) {

            EmbedBuilder assignmentEmbed = new EmbedBuilder()
                    .setTitle(assignment.getName())
                    .setUrl(assignment.getHtmlUrl())
                    .setColor(Color.RED)
                    .setAuthor(DiscordBot.getBotInstance().getApi().getYourself()) //UGLY
                    .addField("Due",
                            (assignment.getDueAt() != null) ? assignment.getDueAt().toString() : "No due date.")
                    .setDescription(
                            (assignment.getDescription() != null) ? assignment.getDescription() : assignment.getName());

            assignmentEmbeds.add(assignmentEmbed);
        }

        return assignmentEmbeds;
    }

    @Override
    protected SlashCommandBuilder getBuilder(Instance instance) {
        return SlashCommand.with(getName(), getShortDescription(), Arrays.asList(
                SlashCommandOption.createSubcommand("search", "input the name of an assignment to be found",
                        Collections.singletonList(SlashCommandOption.create(SlashCommandOptionType.STRING, "search", "name or description of assignment", true))),
                SlashCommandOption.createSubcommand("details", "input the name of an assignment to see details",
                        Collections.singletonList(SlashCommandOption.create(SlashCommandOptionType.STRING, "details", "name or description of assignment", true))),
                SlashCommandOption.createSubcommand("list", "list the active assignments")));
    }

    @Override
    public void onButtonClick(ButtonClickEvent buttonClickEvent) {
        ButtonInteraction interaction = buttonClickEvent.getButtonInteraction();
        String buttonID = interaction.getCustomId();

        if (!buttonID.equals("forward")&&!buttonID.equals("back")){
            return;
        }

        ComponentInteractionOriginalMessageUpdater response = interaction.createOriginalMessageUpdater();

        List<LowLevelComponent> buttons = new ArrayList<>();

        int pageStart = currentPage * assignmentsPerPage;
        int pageEnd;

        if (buttonID.equals("forward") && assignments.size()-(pageStart+assignmentsPerPage)>0){
            currentPage+=1;
            buttons.add(Button.danger("back","◀"));

            if(assignments.size()-((currentPage * assignmentsPerPage)+assignmentsPerPage)>0){ //ugly yo
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
        pageEnd = Math.min(pageStart + assignmentsPerPage, assignments.size());


        response.removeAllEmbeds();
        for (EmbedBuilder embed : assignmentEmbeds().subList(pageStart, pageEnd)) {
            response.addEmbed(embed);
        }

        response.addEmbed(new EmbedBuilder().setFooter("Page "+(currentPage+1) +" ("+pageEnd+"/"+assignments.size()+") results"));
        response.addComponents(ActionRow.of(buttons)).update();
    }
}
