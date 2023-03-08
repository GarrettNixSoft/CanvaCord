package org.canvacord.discord;

import java.util.List;

public class CanvaCordNotification {

	private long channelID;
	private List<CanvaCordRole> rolesToPing;

	public CanvaCordNotification(long channelID, List<CanvaCordRole> rolesToPing) {
		this.channelID = channelID;
		this.rolesToPing = rolesToPing;
	}

	public long getChannelID() {
		return channelID;
	}

	public List<CanvaCordRole> getRolesToPing() {
		return rolesToPing;
	}

	public void setChannelID(long channelID) {
		this.channelID = channelID;
	}

	public void setRolesToPing(List<CanvaCordRole> rolesToPing) {
		this.rolesToPing = rolesToPing;
	}

}
