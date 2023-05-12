package org.canvacord.discord.initialize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.discord.DiscordBot;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.instance.Instance;
import org.canvacord.util.data.DataStructureConversion;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.permission.RoleBuilder;
import org.javacord.api.entity.server.Server;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class RoleRegistration {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void registerRoles(Instance instance) {

		LOGGER.info("Registering roles for instance " + instance.getName());

		// Grab the roles we need to register and their ID map
		List<CanvaCordRole> configuredRoles = instance.getConfiguredRoles();

		// Get the Discord API reference and use it to fetch the target server and make a RoleBuilder
		DiscordApi api = DiscordBot.getBotInstance().getApi();
		Server server = api.getServerById(instance.getServerID()).orElseThrow();
		RoleBuilder roleBuilder = new RoleBuilder(server);

		// Build a map of all existing roles
		Map<String, Role> existingRoles = new DataStructureConversion<String, Role>().listToMap(server.getRoles(), Nameable::getName);

		// Iterate over the configured roles
		for (CanvaCordRole role : configuredRoles) {
			// If this role already exists in the server, skip it
			if (existingRoles.containsKey(role.getName())) {
				if (existingRoles.get(role.getName()).getColor().get().equals(role.getColor())) {
					LOGGER.debug("Found existing role " + role.getName() + ", skipping");
					continue;
				}
			}
			// Assign the role values to the builder
			roleBuilder.setName(role.getName());
			roleBuilder.setColor(role.getColor());
			// Create the role on Discord
			Role createdRole = roleBuilder.create().join();
			// Save the ID
			role.setRoleID(createdRole.getId());
			// Log the role creation
			LOGGER.debug("Created role " + role.getName());
		}

		// Save the roles
		instance.getConfiguration().saveRoles();

	}

}
