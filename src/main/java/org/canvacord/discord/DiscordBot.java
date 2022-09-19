package org.canvacord.discord;

import org.canvacord.util.file.FileUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DiscordBot {

	private final DiscordApiBuilder apiBuilder;
	private DiscordApi api;

	public DiscordBot() {

		// fetch the token
		File tokenFile = Paths.get("resources/token_discord.txt").toFile();
		String token = FileUtil.getFileData(tokenFile).get(0);

		// prepare to log in
		apiBuilder = new DiscordApiBuilder().setToken(token);

	}

	public boolean login() {
		try {
			api = apiBuilder.login().get();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean disconnect() {
		api.disconnect();
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

}
