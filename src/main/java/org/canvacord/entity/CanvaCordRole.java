package org.canvacord.entity;

import org.canvacord.util.CanvaCordColors;
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

	public CanvaCordRole(Color color, String name, long roleID) {
		this.color = color;
		this.name = name;
		this.roleID = roleID;
	}

	public CanvaCordRole(JSONObject roleJSON) {
		try {
			this.name = roleJSON.getString("name");
			this.color = CanvaCordColors.decode(roleJSON.getString("color"));
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
		if (roleID != -1) result.put("id", roleID);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CanvaCordRole other) {
			return this.name.equals(other.name) && this.color.equals(other.color);
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
