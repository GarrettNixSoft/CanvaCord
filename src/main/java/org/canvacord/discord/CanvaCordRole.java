package org.canvacord.discord;

import org.canvacord.util.input.UserInput;
import org.json.JSONObject;

import java.awt.*;

public class CanvaCordRole {

	private Color color;
	private String name;
	private long roleID = -1;

	public CanvaCordRole(Color color, String name) {
		this.color = color;
		this.name = name;
	}

	public CanvaCordRole(JSONObject roleJSON) {
		try {
			this.name = roleJSON.getString("name");
			this.color = Color.decode(roleJSON.getString("color"));
		}
		catch (Exception e) {
			UserInput.showExceptionWarning(e);
		}
	}

	public void setRoleID(long roleID) {
		this.roleID = roleID;
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

	public boolean verified() {
		return roleID != -1;
	}

	public long getRoleID() {
		return roleID;
	}

	public JSONObject getJSON() {
		JSONObject result = new JSONObject();
		result.put("name", name);
		result.put("color", Integer.toHexString(color.getRGB()));
		return result;
	}
}
