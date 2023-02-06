package org.canvacord.instance;

import org.canvacord.exception.CanvaCordException;
import org.json.JSONObject;

public class InstanceParser {

	public static Instance parseInstance(String instanceID, JSONObject instanceJSON) throws CanvaCordException {

		String storedID = instanceJSON.getString("id");

		if (!storedID.equals(instanceID))
			throw new CanvaCordException("Instance file name and stored ID do not match! (" + instanceID + ", " + storedID + ")");

		String serverIdStr = instanceJSON.getString("server_id");
		long serverID = Long.parseLong(serverIdStr);

		String courseID = instanceJSON.getString("course_id");

		JSONObject configurationJSON = instanceJSON.getJSONObject("config");
		InstanceConfiguration configuration = new InstanceConfiguration(configurationJSON);

		try {
			return new Instance(courseID, serverID, configuration);
		}
		catch (InstantiationException e) {
			throw new CanvaCordException(e.getMessage());
		}
	}

}