package org.canvacord.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canvacord.cli.CanvaCordCLI;
import org.canvacord.discord.DiscordBot;
import org.canvacord.discord.commands.CommandHandler;
import org.canvacord.exception.ExplosionHandler;
import org.canvacord.gui.CanvaCordApp;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.canvacord.reminder.ReminderManager;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.canvacord.setup.FirstRunSetup;
import org.canvacord.util.LookAndFeel;
import org.canvacord.util.input.UserInput;
import org.json.JSONObject;

import java.util.Optional;

public class CanvaCord {

	private static final Logger LOGGER = LogManager.getLogger(CanvaCord.class);

	public static final String VERSION_ID = "v0.0.1";

	public static void main(String[] args) {

		// examine args
		if (args.length > 0) {
			if (args[0].equals("nogui")) {
				LOGGER.info("Running CanvaCord in headless mode.");
				runHeadless();
			}
			else printUsage();
		}

		// no arguments, default to run with GUI
		else runWithGUI();

	}

	private static void runWithGUI() {
		// make CanvaCord look native
		LookAndFeel.init();

		// check whether the setup process has been run before
		checkSetup();

		// initialize CanvaCord components
		init();

		// run the application!
		CanvaCordApp.run();
	}

	private static void runHeadless() {

		// initialize CanvaCord components
		init();

		// run the application in headless mode!
		CanvaCordCLI.runCLI();

	}

	private static void init() {
		DiscordBot.getBotInstance().login();
		InstanceManager.loadInstances();
		CanvaCordScheduler.init();
		ReminderManager.init();
		CommandHandler.init();
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

	public static void explode() {

		try {
			ExplosionHandler.makeFunnyBoomSound();
			UserInput.showErrorMessage("CanvaCord has exploded.", "Catastrophic Failure");
//			Thread.sleep(1000);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Something broke the funny boom sound, what a party pooper.");
		}

		System.exit(-1);

	}

	public static void explode(String message) {

		try {
			ExplosionHandler.makeFunnyBoomSound();
			UserInput.showErrorMessage(message, "Catastrophic Failure");
//			Thread.sleep(1000);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Something broke the funny boom sound, what a party pooper.");
		}

		System.exit(-1);

	}

	private static void printUsage() {
		System.out.println("Usage: CanvaCord.jar [nogui]");
	}

}
