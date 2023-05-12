package org.canvacord.discord.commands;

import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.javacord.api.entity.message.Message;
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
	private List<Assignment> assignments;


	private final List<SlashCommandOptionChoice> slashCommandBucketChoices = Arrays.asList(
			SlashCommandOptionChoice.create("Past","past"),
			SlashCommandOptionChoice.create("Undated","undated"),
			SlashCommandOptionChoice.create("Upcoming","upcoming"),
			SlashCommandOptionChoice.create("Future","future"));

	private final Map<String, ListCourseAssignmentsOptions.Bucket> bucketsFromString = Map.of(
			"past", ListCourseAssignmentsOptions.Bucket.PAST,
			"undated",ListCourseAssignmentsOptions.Bucket.UNDATED,
			"upcoming",ListCourseAssignmentsOptions.Bucket.UPCOMING,
			"future",ListCourseAssignmentsOptions.Bucket.FUTURE);

	@Override
	public String getDescription() {
		return """
                The assignment command fetches assignments from the Canvas course and outputs them in a variety of ways.
                
                **Parameters:**
                ***/assignment search <parameter> bucket <(optional) bucket>***
                The *search* subcommand allows you to input a string (must be 2 characters or longer) which queries the course assignments for titles that match the parameters.
                
                ***/assignment details <parameter> bucket <(optional) bucket>***
                The *details* subcommand operates like a search, but instead it gives you the best match in greater detail.
                
                ***/assignment list bucket <(optional) bucket>***
                The *list* subcommand finds all assignments matching the bucket and outputs them in a list. (defaults to "future")
                
                **WHAT ARE BUCKETS?**
                Buckets are a means by which to organize the assignments stored within Canvas! The allowed assignment command buckets are based on due date.
                
                **Past:** Assignments past the due date.
                **Undated:** Assignments that have no date assigned in Canvas.
                **Upcoming:** Assignments that are due soon.
                **Future:** Assignments where the due date has not yet occurred.
                """;
	}

	@Override
	public String getShortDescription() {
		return "View course assignments";
	}

	@Override
	public String getName() {
		return "assignment";
	}

	@Override
	public void execute(SlashCommandInteraction interaction) {

		// reset these each time the command is called
		currentPage = 0;
		assignments = new ArrayList<>();

		Server server = interaction.getServer().orElseThrow(() -> new CanvaCordException("Server not found"));
		instance = InstanceManager.getInstanceByServerID(server.getId()).orElseThrow(()->new CanvaCordException("Instance not found"));
		CanvasApi canvasApi = CanvasApi.getInstance();
		String courseID = instance.getCourseID();

		// Use respondLater to send a response that takes longer than 3 seconds
		// set the value to true to make it ephemeral
		interaction.respondLater(true).thenAccept(interactionOriginalResponseUpdater -> {
			String arguments = interaction.getArgumentStringValueByName("search").orElse(
					interaction.getArgumentStringValueByName("details").orElse(null));
			String bucket = interaction.getArgumentStringValueByName("bucket").orElse("future");

			if (arguments != null && arguments.length()<2){
				interactionOriginalResponseUpdater.addEmbed(
						new EmbedBuilder()
								.setColor(Color.RED)
								.setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
								.addField("Error","Search argument length must be greater than 2 characters!")).update();
				return;
			} // could maybe handle this a different way

			try {
				assignments = canvasApi.getAssignmentsOptions(courseID,arguments,bucketsFromString.get(bucket));
			} catch (IOException e) {
				interactionOriginalResponseUpdater.addEmbed(
						new EmbedBuilder()
								.setColor(Color.RED)
								.setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
								.addField("Error","An error has occurred! Results may be affected.")).update();
				e.printStackTrace();
			}

			if (assignments.size()==0){
				interactionOriginalResponseUpdater.addEmbed(
						new EmbedBuilder()
								.setColor(Color.RED)
								.setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
								.addField("No Results","The requested search results are empty. Please try again with different parameters.")).update();
			}
			else if (!interaction.getFullCommandName().contains("details")){
				sendAssignmentList(interactionOriginalResponseUpdater);
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
				.addInlineField("Points Possible", String.valueOf(assignment.getPointsPossible()))
				.addInlineField("Submission Types", assignment.getSubmissionTypes().toString())
		);
		interactionOriginalResponseUpdater.update();

	}
	private void sendAssignmentList(InteractionOriginalResponseUpdater interactionOriginalResponseUpdater){
		// get the minimum between the default value or the num of fetched assignments

		int pageEnd = Math.min(assignmentsPerPage,assignments.size());
		for (EmbedBuilder embed : assignmentEmbeds().subList(0, pageEnd)) {
			interactionOriginalResponseUpdater.addEmbed(embed).update();
		}

		interactionOriginalResponseUpdater.addEmbed(new EmbedBuilder().setFooter("Page "+(currentPage+1) +" ("+pageEnd+"/"+assignments.size()+") results"));

		if (assignments.size()>assignmentsPerPage){
			Message response = interactionOriginalResponseUpdater
					.addComponents(ActionRow.of(Button.danger("forward", "▶")))
					.update().join();
			response.addButtonClickListener(this).removeAfter(10, TimeUnit.MINUTES);
		}
	}

	private List<EmbedBuilder> assignmentEmbeds() {

		List<EmbedBuilder> assignmentEmbeds = new ArrayList<>();

		for (Assignment assignment : assignments) {

			EmbedBuilder assignmentEmbed = new EmbedBuilder()
					.setTitle(assignment.getName())
					.setUrl(assignment.getHtmlUrl())
					.setColor(Color.RED)
					.setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
					.addField("Due",
							(assignment.getDueAt() != null) ? assignment.getDueAt().toString() : "No due date.")
					.setDescription(
							(assignment.getDescription() != null) ? assignment.getDescription() : assignment.getName());

			assignmentEmbeds.add(assignmentEmbed);
		}

		return assignmentEmbeds;
	}

	@Override
	public SlashCommandBuilder getBuilder(Instance instance) {
		return SlashCommand.with(getName(), getShortDescription(), Arrays.asList(
				SlashCommandOption.createSubcommand("search", "input the name of an assignment to be found",
						Arrays.asList(SlashCommandOption.createStringOption( "search", "name or description of assignment", true),
								SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,"bucket","(optional) Pick a bucket to organize the found assignments",false,
										slashCommandBucketChoices))),
				SlashCommandOption.createSubcommand("details", "input the name of an assignment to see details",
						Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "details", "name or description of assignment", true),
								SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,"bucket","(optional) Pick a bucket to organize the found assignments.",false,
										slashCommandBucketChoices))),
				SlashCommandOption.createSubcommand("list", "list the active assignments",
						Collections.singletonList(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,"bucket","(optional) Pick a bucket to organize the found assignments.",false,
								slashCommandBucketChoices)))));
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
