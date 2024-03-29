package org.canvacord.discord;

import org.canvacord.discord.commands.CommandHandler;
import org.canvacord.persist.ConfigManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionException;

public class DiscordBot {

	private static DiscordBot botInstance;

	private final DiscordApiBuilder API_BUILDER;
	private DiscordApi api;

	private boolean connected = false;

	private DiscordBot() {

		// fetch the token
		String token = ConfigManager.getDiscordToken();

		// prepare to log in
		API_BUILDER = new DiscordApiBuilder().setToken(token);

	}

	private DiscordBot(String tokenStr) {
		API_BUILDER = new DiscordApiBuilder().setToken(tokenStr);
	}

	public static DiscordBot getBotInstance() {
		if (botInstance == null) {
			botInstance = new DiscordBot(ConfigManager.getDiscordToken());
		}
		return botInstance;
	}

	public boolean login() {
		try {
			api = API_BUILDER.login().join();
			connected = true;
			api.addSlashCommandCreateListener(event -> CommandHandler.executeCommand(event.getSlashCommandInteraction()));
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean disconnect() {
		try {
			api.disconnect().join();
			connected = false;
		}
		catch (CompletionException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getInviteLink() {
		return api.createBotInvite();
	}

	public String getInviteLink(Permissions permissions) {
		return api.createBotInvite(permissions);
	}

	public Collection<Server> getServerMemberships() {
		return api.getServers();
	}

	//send assignment message to target channel
	public void sendMessageToChannel(String message, long channelID) {
		TextChannel targetChannel = (TextChannel) this.getApi().getChannelById(channelID).get();
		MessageBuilder test = new MessageBuilder();
		test.append(message).send(targetChannel);
	}

	// IDK
	public DiscordApi getApi() {
		return api;
	}

	public Optional<Server> getServerByID(long serverID) {
		if (!connected) login();
		return api.getServerById(serverID);
	}

	/**
	 * Used to verify users' bot tokens. Simply takes the
	 * token, attempts to log into Discord with it, and
	 * returns a boolean indicating whether that worked.
	 * @param tokenStr the token to test
	 * @return {@code true} if the token works for logging into Discord
	 */
	public static boolean testTokenString(String tokenStr) {

		DiscordBot testBot = new DiscordBot(tokenStr);
		boolean works = testBot.login();
		if (works) {
			testBot.disconnect();
			return true;
		}
		else return false;

	}

}
