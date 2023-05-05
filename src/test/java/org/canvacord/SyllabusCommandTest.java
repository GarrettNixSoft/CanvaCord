package org.canvacord;

import org.canvacord.discord.DiscordBot;
import org.canvacord.discord.commands.RemindMeCommand;
import org.canvacord.discord.commands.SyllabusCommand;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class SyllabusCommandTest {

	public static void main(String[] args) {

		// Load config and prepare server ID
		ConfigManager.loadConfig();
		InstanceManager.loadInstances();
		long serverID = 1016848330992656415L;

		// Log the bot in
		DiscordBot.getBotInstance().login();

		// Grab the API
		DiscordApi api = DiscordBot.getBotInstance().getApi();

		SyllabusCommand command = new SyllabusCommand();

		// Create the command in the target server
		command.getBuilder(null)
				.createForServer(api.getServerById(serverID).get())
				.join();

		System.out.println("Created command");

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction interaction = event.getSlashCommandInteraction();
			if (interaction.getFullCommandName().toLowerCase().contains("syllabus"))
				command.execute(event.getSlashCommandInteraction());
		});

	}

}
