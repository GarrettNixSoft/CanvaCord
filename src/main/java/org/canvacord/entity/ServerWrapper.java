package org.canvacord.entity;

import org.javacord.api.entity.server.Server;

public record ServerWrapper(Server server) {
	@Override
	public String toString() {
		return server.getName();
	}
}
