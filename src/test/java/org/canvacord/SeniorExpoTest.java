package org.canvacord;

import org.canvacord.discord.DiscordBot;
import org.canvacord.discord.commands.*;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class SeniorExpoTest {

	public static void main(String[] args) {

		// Load config and prepare server ID
		ConfigManager.loadConfig();
		InstanceManager.loadInstances();
		long serverID = 1016848330992656415L;

		// Log the bot in
		DiscordBot.getBotInstance().login();

		// Grab the API
		DiscordApi api = DiscordBot.getBotInstance().getApi();

		// prepare commands
		AssignmentCommand assignmentCommand = new AssignmentCommand();
		SyllabusCommand syllabusCommand = new SyllabusCommand();
		ModuleCommand moduleCommand = new ModuleCommand();
		TextbookCommand textbookCommand = new TextbookCommand();
		TextbookFinderCommand textbookFinderCommand = new TextbookFinderCommand();
		HelpCommand helpCommand = new HelpCommand();

		Instance instance = InstanceManager.getInstances().get(0);

		textbookCommand.getBuilder(instance)
				.createForServer(api.getServerById(serverID).get());
		System.out.println("Created command");

		textbookFinderCommand.getBuilder(instance)
						.createForServer(api.getServerById(serverID).get());
		System.out.println("Created other command");

		helpCommand.getBuilder(instance)
				.createForServer(api.getServerById(serverID).get());
		System.out.println("Created help command");

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction interaction = event.getSlashCommandInteraction();
			if (interaction.getFullCommandName().toLowerCase().contains("assignment"))
				assignmentCommand.execute(event.getSlashCommandInteraction());
			else if (interaction.getFullCommandName().toLowerCase().contains("syllabus"))
				syllabusCommand.execute(event.getSlashCommandInteraction());
			else if (interaction.getFullCommandName().toLowerCase().contains("module"))
				moduleCommand.execute(event.getSlashCommandInteraction());
			else if (interaction.getFullCommandName().toLowerCase().contains("textbookfinder"))
				textbookFinderCommand.execute(event.getSlashCommandInteraction());
			else if (interaction.getFullCommandName().toLowerCase().contains("textbook"))
				textbookCommand.execute(event.getSlashCommandInteraction());
			else if (interaction.getFullCommandName().toLowerCase().contains("help"))
				helpCommand.execute(event.getSlashCommandInteraction());
		});

	}

}
