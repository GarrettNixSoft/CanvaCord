package org.canvacord.instance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.persist.CacheManager;
import org.canvacord.util.file.CanvaCordPaths;

import java.nio.file.Files;

public class InstanceCleanUp {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void runInstanceCleanup(Instance instance) {
		// delete the cache data
		CacheManager.clearCacheForInstance(instance);
		try {
			// delete the data file
			Files.delete(CanvaCordPaths.getInstanceCachePath(instance));
			// delete the reminders file
			Files.delete(CanvaCordPaths.getInstanceRemindersPath(instance));
			// mark the instance as cleaned up
			instance.getConfiguration().markCleanedUp();
		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			LOGGER.error(e.getStackTrace());
		}
		// TODO archive the Discord server
	}

}
