package org.canvacord.discord;

import java.awt.*;

public class CanvaCordRole {

	private Color color;
	private String name;

	public CanvaCordRole(Color color, String name) {
		this.color = color;
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
