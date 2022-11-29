package org.canvacord.discord;

import org.canvacord.util.file.FileUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletionException;

public class DiscordBot {

	private static DiscordBot botInstance;

	private final DiscordApiBuilder API_BUILDER;
	private DiscordApi api;

	private DiscordBot() {

		// fetch the token
		File tokenFile = Paths.get("config/token_discord.txt").toFile();
		String token = FileUtil.getFileData(tokenFile).get(0);

		// prepare to log in
		API_BUILDER = new DiscordApiBuilder().setToken(token);

	}

	public static DiscordBot getBotInstance() {
		if (botInstance == null)
			botInstance = new DiscordBot();
		return botInstance;
	}

	public boolean login() {
		try {
			api = API_BUILDER.login().get();
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



	// IDK
	public DiscordApi getApi() {
		return api;
	}

}
