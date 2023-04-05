package org.canvacord;

import org.canvacord.discord.DiscordBot;
import org.canvacord.discord.commands.Command;
import org.canvacord.discord.commands.RemindMeCommand;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.canvacord.reminder.ReminderManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class RemindMeCommandTest {

	public static void main(String[] args) {

		// Load config and prepare server ID
		ConfigManager.loadConfig();
		InstanceManager.loadInstances();
		ReminderManager.init();
		long serverID = 1016848330992656415L;

		// Log the bot in
		DiscordBot.getBotInstance().login();

		// Grab the API
		DiscordApi api = DiscordBot.getBotInstance().getApi();

		RemindMeCommand command = new RemindMeCommand();

		// Create the command in the target server
		command.getBuilder()
				.createForServer(api.getServerById(serverID).get())
				.join();

		System.out.println("Created command");

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction interaction = event.getSlashCommandInteraction();
			if (interaction.getFullCommandName().toLowerCase().contains("remind"))
				command.execute(event.getSlashCommandInteraction());
		});

	}

}
