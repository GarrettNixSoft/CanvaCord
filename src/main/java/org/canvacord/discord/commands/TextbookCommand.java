package org.canvacord.discord.commands;

import org.canvacord.canvas.TextbookFetcher;
import org.canvacord.canvas.TextbookInfo;
import org.canvacord.discord.DiscordBot;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.Globals;
import org.canvacord.util.string.StringUtils;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TextbookCommand extends Command {

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
		return "textbook";
	}
	@Override
	public SlashCommandBuilder getBuilder(Instance instance){
		return SlashCommand.with(
				getName(),
				getShortDescription(),
				List.of(
						SlashCommandOption.createWithOptions(
								SlashCommandOptionType.SUB_COMMAND,
								"search",
								"Search online for a textbook",
								List.of(
										SlashCommandOption.create(
										SlashCommandOptionType.STRING,
										"title",
										"The title to search for",
										true
										)
								)
						),
						SlashCommandOption.create(
								SlashCommandOptionType.SUB_COMMAND,
								"list",
								"List all textbooks for this course"
								),
						SlashCommandOption.createWithOptions(
								SlashCommandOptionType.SUB_COMMAND,
								"get",
								"Get a textbook from this course",
								List.of(
										SlashCommandOption.create(
												SlashCommandOptionType.STRING,
												"title",
												"The title to search for",
												true
										)
								)
						)
				));
	}

	@Override
	public void execute(SlashCommandInteraction interaction) {

		// Check the Instance context for this command
		Optional<Server> server = interaction.getServer();
		if (server.isEmpty()) {
			interaction.createImmediateResponder().setContent("Could not fetch the server context. Please contact the server Owner.").respond();
			return;
		}

		Optional<Instance> instanceOpt = InstanceManager.getInstanceByServerID(server.get().getId());
		if (instanceOpt.isEmpty()) {
			interaction.createImmediateResponder().setContent("Could not fetch the CanvaCord instance for this server. Please contact the server Owner.").respond();
			return;
		}

		// The instance should be available
		Instance instance = instanceOpt.get();

		// Check that this instance textbook commands
		if (!instance.getAvailableCommands().containsKey("textbooks")) {
			interaction.createImmediateResponder().setContent("This CanvaCord instance has disabled textbook commands.").respond();
			return;
		}

		// Grab the subcommand value
		SlashCommandInteractionOption subcommand = interaction.getOptionByIndex(0).orElseThrow();

		// Use a try/catch to avoid any exceptions preventing a response
		try {
			// Branch based on the subcommand name
			switch (subcommand.getName()) {
				case "search" -> {

					// Grab the search term
					SlashCommandInteractionOption searchTermOption = interaction.getOptionByIndex(0).orElseThrow().getOptionByIndex(0).orElseThrow();
					String searchTerm = searchTermOption.getStringValue().orElseThrow().toLowerCase();

					// Prepare to respond in the future
					CompletableFuture<InteractionOriginalResponseUpdater> response;
					// Use respondLater to send a response that takes longer than 3 seconds
					// set the value to true to make it ephemeral
					response = interaction.respondLater(true);

					// Run the actual response code
					response.thenAccept(interactionOriginalResponseUpdater -> {

						// Download a textbook
						Globals.EDIT_INSTANCE_ID = instance.getInstanceID();
						Optional<TextbookInfo> textbookDummy = TextbookFetcher.fetchTextbookOnline(searchTerm, instance.getCourseID());

						// If the download succeeded, send the new file
						if(!instance.getTextbooks().isEmpty()){

							textbookDummy.ifPresentOrElse(textbookData -> {
								interactionOriginalResponseUpdater
										.addEmbed(new EmbedBuilder().setTitle(textbookData.getTitle() + " Textbook")
												.setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
												.setColor(Color.RED)
												.addField("Course ",instance.getCourseTitle()))
										.addAttachment(textbookData.getTextbookFile()).update();
							}, () -> interactionOriginalResponseUpdater.addEmbed(errorMessage).update());
						}
						// Otherwise send an error message
						else {
							interactionOriginalResponseUpdater.addEmbed(errorMessage).update();
						}
					});

				}
				case "list" -> {

					// Grab all the textbooks
					Globals.EDIT_INSTANCE_ID = instance.getInstanceID();
					List<TextbookInfo> textbooks = instance.getTextbooks();

					// If there are none, let the user know
					if (textbooks.isEmpty()) {
						interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("No textbooks have been added for this course.").respond();
					}
					else {
						// If there are few enough to fit, send them
						if (textbooks.size() <= 10) {

							// Prepare to respond in the future
							CompletableFuture<InteractionOriginalResponseUpdater> response;
							// Use respondLater to send a response that takes longer than 3 seconds
							// set the value to true to make it ephemeral
							response = interaction.respondLater(true);

							response.thenAccept(interactionOriginalResponseUpdater -> {
								for (TextbookInfo textbookInfo : textbooks) {
									EmbedBuilder embedBuilder = new EmbedBuilder();
									embedBuilder.setTitle(textbookInfo.getTitle() + " Textbook")
											.setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
											.setColor(Color.RED)
											.addField("Course ", instance.getCourseTitle());
									interactionOriginalResponseUpdater.addEmbed(embedBuilder);
									interactionOriginalResponseUpdater.addAttachment(textbookInfo.getTextbookFile());
								}
								interactionOriginalResponseUpdater.update();
							});
						}
						// Otherwise inform the user that there are too many to show
						else {
							interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("There are too many textbooks configured for this course to list.").respond();
						}

					}

				}
				case "get" -> {

					// Grab all the textbooks
					Globals.EDIT_INSTANCE_ID = instance.getInstanceID();
					List<TextbookInfo> textbooks = instance.getTextbooks();

					// If there are none, let the user know
					if (textbooks.isEmpty()) {
						interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("No textbooks have been added for this course.").respond();
					}
					// Otherwise, check what they searched and find a match
					else {

						// Get their search term
						SlashCommandInteractionOption searchTermOption = interaction.getOptionByIndex(0).orElseThrow().getOptionByIndex(0).orElseThrow();
						String searchTerm = searchTermOption.getStringValue().orElseThrow().toLowerCase();

						// Prepare to find the best match
						int bestScore = 0;
						TextbookInfo bestMatch = null;

						// Iterate
						for (TextbookInfo textbookInfo : textbooks) {
							int score = StringUtils.getSimilarityScore(textbookInfo.getTitle().toLowerCase(), searchTerm);
							if (score > bestScore) {
								bestScore = score;
								bestMatch = textbookInfo;
							}
						}

						if (bestMatch != null) {
							CompletableFuture<InteractionOriginalResponseUpdater> response;
							response = interaction.respondLater(true);
							TextbookInfo finalBestMatch = bestMatch;
							response.thenAccept(interactionOriginalResponseUpdater -> {
								EmbedBuilder embedBuilder = new EmbedBuilder();
								embedBuilder.setTitle(finalBestMatch.getTitle() + " Textbook")
										.setAuthor(DiscordBot.getBotInstance().getApi().getYourself())
										.setColor(Color.RED)
										.addField("Course ", instance.getCourseTitle());
								interactionOriginalResponseUpdater.addAttachment(finalBestMatch.getTextbookFile());
								interactionOriginalResponseUpdater.update();
							});
						}
						else {
							interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("No matches were found.").respond();
						}

						// TODO
					}

				}
				default -> throw new CanvaCordException("Invalid textbook subcommand name: " + subcommand.getName());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("CanvaCord encountered an exception: " + e.getMessage() + "; Please contact the server Owner.").respond();
		}

	}
}
