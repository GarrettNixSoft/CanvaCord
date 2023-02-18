package org.canvacord.main;

import org.canvacord.gui.CanvaCordApp;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.canvacord.setup.FirstRunSetup;
import org.canvacord.util.input.UserInput;
import org.json.JSONObject;

import java.util.Optional;

public class CanvaCord {

	public static final String VERSION_ID = "v0.0.1";

	public static void main(String[] args) {

		// check whether the setup process has been run before
		checkSetup();

		// initialize CanvaCord components
		InstanceManager.loadInstances();
		CanvaCordScheduler.init();

		// run the application!
		CanvaCordApp.run();

	}

	private static void checkSetup() {

		// look for the config JSON file
		Optional<JSONObject> configJSON = ConfigManager.loadConfig();

		// if the config is missing, run the setup process
		if (configJSON.isEmpty()) {

			// check whether the user completed the setup process
			boolean setup = FirstRunSetup.runFirstTimeSetup();

			// if not, inform them that they must do that first and exit
			if (!setup) {
				UserInput.showErrorMessage("""
						The setup process was not completed successfully. In order to use CanvaCord,
						you must complete the initial setup process and provide API tokens. Please
						run CanvaCord again and provide the necessary information to begin.""", "Setup Not Complete");
				System.exit(0);
			}

		}

	}

}
