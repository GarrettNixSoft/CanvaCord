package org.canvacord.setup;

import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.cards.instance.*;
import org.canvacord.instance.InstanceConfiguration;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.json.JSONObject;

import javax.swing.*;
import java.util.Optional;

/**
 * The InstanceCreateWizard implements the process of creating a new CanvaCord
 * instance.
 */
public class InstanceCreateWizard extends CanvaCordWizard {

	private InstanceSetupWelcomeCard startingCard;
	private CourseAndServerCard courseAndServerCard;
	private InstanceBasicConfigCard basicConfigCard;
	private InstanceCanvasFetchCard canvasFetchCard;
	private RoleCreateCard roleCreateCard;

	public InstanceCreateWizard() {
		super("Create Instance");
	}

	private void disableNext() {
		setNextButtonEnabled(false);
		setNextButtonTooltip("<html>You must verify your Course ID and<br>Server ID before continuing.</html>");
	}

	private void enableNext() {
		setNextButtonEnabled(true);
		setNextButtonTooltip(null);
	}

	@Override
	protected void initCards() {

		// The first card is the welcome page
		startingCard = new InstanceSetupWelcomeCard(this, "start");

		// The second card is the Canvas course and Discord server setup page
//		WizardCard courseServerCard = buildCourseAndServerCard();
		courseAndServerCard = new CourseAndServerCard(this, "course_server");

		// The third card is the first page of configuration
		basicConfigCard = new InstanceBasicConfigCard(this, "config_1", false);

		// The fourth card is for setting up the Canvas fetching schedule
		canvasFetchCard = new InstanceCanvasFetchCard(this, "fetch_config", false);

		// The fifth card is for defining what roles this instance should use
		roleCreateCard = new RoleCreateCard(this, "role_config", true);

		// ================================ Configure the navigation connections ================================
		// ================ START ================
		startingCard.setNavigator(() -> Optional.of(courseAndServerCard));
		startingCard.setOnNavigateTo(this::enableNext);

		// ================ COURSE AND SERVER ================
		courseAndServerCard.setNavigator(() -> Optional.of(basicConfigCard));
		courseAndServerCard.setPreviousCard(startingCard);

		courseAndServerCard.setOnNavigateTo(() -> {
			if (!(courseAndServerCard.isVerifiedCanvasCourse() && courseAndServerCard.isVerifiedDiscordServer())) {
				disableNext();
			}
		});

		// ================ NAME AND ICON ================
		basicConfigCard.setNavigator(() -> Optional.of(canvasFetchCard));
		basicConfigCard.setPreviousCard(courseAndServerCard);

		basicConfigCard.setOnNavigateTo(() -> {

			enableNext();

		});

		// ================ FETCH SCHEDULE ================
		canvasFetchCard.setNavigator(() -> Optional.of(roleCreateCard));
		canvasFetchCard.setPreviousCard(basicConfigCard);

		// ================ DISCORD ROLES ================
		roleCreateCard.setNavigator(Optional::empty);
		roleCreateCard.setPreviousCard(canvasFetchCard);

		// Register the cards
		registerCard(roleCreateCard); // TODO this should be last

		registerCard(startingCard);
		registerCard(courseAndServerCard);
		registerCard(basicConfigCard);
		registerCard(canvasFetchCard);
	}

	@Override
	public boolean completedSuccessfully() {

		// TODO
		if (isCancelled())
			return false;

		// for now, only verify that the course and server IDs were verified
		return courseAndServerCard.isVerifiedCanvasCourse() && courseAndServerCard.isVerifiedDiscordServer();

	}

	public InstanceConfiguration getResult() {

		// Store all the settings in a JSON object that will be wrapped in an InstanceConfiguration
		JSONObject configJSON = new JSONObject();

		// Get the course and server IDs from their config page
		String courseID = courseAndServerCard.getCourseID();
		long serverID = courseAndServerCard.getServerID();

		// Put them into the JSON object
		configJSON.put("course_id", courseID);
		configJSON.put("server_id", serverID);

		// Fetch and store the Course and Server names
		configJSON.put("course_title", courseAndServerCard.getCourseTitle());
		configJSON.put("server_name", courseAndServerCard.getServerName());

		// Fetch name and icon path from the basic settings card
		String name = basicConfigCard.getInstanceName();
		if (!name.isBlank())
			configJSON.put("name", name);

		String iconPath = basicConfigCard.getIconPath();
		if (!iconPath.isBlank())
			if (FileUtil.isValidFile(iconPath, "png", "jpg", "jpeg"))
				configJSON.put("icon_path", iconPath);
			else
				UserInput.showMessage("Could not load the specified image.\nUsing default icon.", "Bad Icon Path");

		JSONObject scheduleObject = canvasFetchCard.getScheduleJSON();
		configJSON.put("canvas_fetch_schedule", scheduleObject);

		// TODO add more settings from other pages

		// Wrap it all up in a nice InstanceConfiguration object for convenience
		return new InstanceConfiguration(configJSON);

	}

}
