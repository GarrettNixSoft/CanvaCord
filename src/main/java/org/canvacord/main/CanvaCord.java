package org.canvacord.main;

import org.canvacord.discord.DiscordBot;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

import java.util.Scanner;

public class CanvaCord {

	public static void main(String[] args) {

		// placeholder
		System.out.println("Hello world!");

		// create a bot instance
		DiscordBot bot = DiscordBot.getBotInstance();
		System.out.println("Bot initialized!");

		// connect it to Discord
		boolean login = bot.login();
		if (login) {
			System.out.println("Bot logged in!");
			System.out.println("Invite link: " + bot.getInviteLink());

			System.out.println("The bot is a member of the following servers:");
			for (Server server : bot.getServerMemberships()) {
				System.out.println(server.getName());
			}

			String input;
			Scanner in = new Scanner(System.in);
			input = in.nextLine();

			TextChannel channel = bot.getApi().getChannelById(1039693805865152593L).get().asTextChannel().get();

			while (!input.equalsIgnoreCase("q")) {

				channel.sendMessage(input);
				input = in.nextLine();

			}

			if (bot.disconnect())
				System.out.println("Bot disconnected!");
			else
				System.err.println("Disconnect failed!");

			in.close();

		}
		else {
			System.err.println("Login failed.");
			System.exit(-1);
		}



	}

}
