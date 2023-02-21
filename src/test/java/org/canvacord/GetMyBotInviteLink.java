package org.canvacord;

import org.canvacord.discord.DiscordBot;

public class GetMyBotInviteLink {

	public static void main(String[] args) {

		DiscordBot bot = DiscordBot.getBotInstance();
		bot.login();
		System.out.println(bot.getInviteLink());
		bot.disconnect();

	}

}
