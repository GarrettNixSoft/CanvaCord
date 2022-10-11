package org.canvacord.main;

import org.canvacord.discord.DiscordBot;
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

			Scanner input = new Scanner(System.in);
			input.nextLine();

			bot.disconnect();
			System.out.println("Bot disconnected!");

			input.close();

		}
		else {
			System.out.println("Login failed.");
			System.exit(-1);
		}

	}

}
