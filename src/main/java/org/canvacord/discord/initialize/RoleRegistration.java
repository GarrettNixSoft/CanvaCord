package org.canvacord.discord.initialize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.instance.Instance;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.RoleBuilder;
import org.javacord.api.entity.server.Server;

import java.util.List;

public class RoleRegistration {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void registerRoles(Instance instance) {

		LOGGER.info("Registering roles for instance " + instance.getName());

		// Grab the roles we need to register
		List<CanvaCordRole> rolesToRegister = instance.getConfiguredRoles();

		// Get the Discord API reference and use it to fetch the target server and make a RoleBuilder
		DiscordApi api = DiscordBot.getBotInstance().getApi();
		Server server = api.getServerById(instance.getServerID()).orElseThrow();
		RoleBuilder roleBuilder = new RoleBuilder(server);

		// Iterate over the configured roles
		for (CanvaCordRole role : rolesToRegister) {

			// Assign the role values to the builder
			roleBuilder.setName(role.getName());
			roleBuilder.setColor(role.getColor());
			// Create the role on Discord
			roleBuilder.create().join();
			// Log the role creation
			LOGGER.debug("Created role " + role.getName());

		}

	}

}
