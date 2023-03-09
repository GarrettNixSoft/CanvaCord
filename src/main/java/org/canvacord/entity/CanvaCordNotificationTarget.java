package org.canvacord.entity;

import org.javacord.api.entity.channel.ServerTextChannel;

public record CanvaCordNotificationTarget(ServerTextChannel serverChannel) {

	public long id() {
		return serverChannel.getId();
	}

	@Override
	public String toString() {
		return serverChannel.getName();
	}
}
