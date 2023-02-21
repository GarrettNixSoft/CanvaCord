package org.canvacord.instance;

import org.canvacord.exception.CanvaCordException;
import org.json.JSONObject;

public class InstanceParser {

	public static Instance parseInstance(String instanceID, JSONObject instanceJSON) throws CanvaCordException {

		long serverID = instanceJSON.getLong("server_id");
		String courseID = instanceJSON.getString("course_id");
		String storedID = courseID + "-" + serverID;

		if (!storedID.equals(instanceID))
			throw new CanvaCordException("Instance file name and stored ID do not match! (" + instanceID + ", " + storedID + ")");

		InstanceConfiguration configuration = new InstanceConfiguration(instanceJSON);

		try {
			return new Instance(courseID, serverID, configuration);
		}
		catch (InstantiationException e) {
			throw new CanvaCordException(e.getMessage());
		}
	}

}
