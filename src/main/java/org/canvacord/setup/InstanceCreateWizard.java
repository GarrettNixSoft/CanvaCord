package org.canvacord.setup;

import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.cards.instance.CourseAndServerCard;
import org.canvacord.gui.wizard.cards.instance.InstanceBasicConfigCard;
import org.canvacord.gui.wizard.cards.instance.InstanceSetupWelcomeCard;
import org.canvacord.instance.InstanceConfiguration;
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
	private InstanceBasicConfigCard configCard;

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
		configCard = new InstanceBasicConfigCard(this, "config_1", true);

		// Configure the navigation connections
		startingCard.setNavigator(() -> Optional.of(courseAndServerCard));

		startingCard.setOnNavigateTo(this::enableNext);

		courseAndServerCard.setNavigator(() -> Optional.of(configCard));
		courseAndServerCard.setPreviousCard(startingCard);

		courseAndServerCard.setOnNavigateTo(() -> {
			if (!(courseAndServerCard.isVerifiedCanvasCourse() && courseAndServerCard.isVerifiedDiscordServer())) {
				disableNext();
			}

			System.out.println("Component 0: " + courseAndServerCard.getComponent(0));
			System.out.println("Component 0's component 0: " + ((JPanel) courseAndServerCard.getComponent(0)).getComponent(0));
			System.out.println("Component 0's component 0's component 0: " + ((JPanel) ((JPanel) courseAndServerCard.getComponent(0)).getComponent(0)).getComponent(0));
		});

		configCard.setNavigator(Optional::empty);
		configCard.setPreviousCard(courseAndServerCard);

		configCard.setOnNavigateTo(() -> {

			enableNext();

			System.out.println("Component 0: " + configCard.getComponent(0));
			System.out.println("Component 0's component 0: " + ((JPanel) configCard.getComponent(0)).getComponent(0));
			System.out.println("Component 0's component 0's component 0: " + ((JPanel) ((JPanel) configCard.getComponent(0)).getComponent(0)).getComponent(0));

		});

		// Register the cards
		registerCard(startingCard);
		registerCard(courseAndServerCard);
		registerCard(configCard);
	}

	@Override
	public boolean completedSuccessfully() {

		// TODO

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

		// TODO add more settings from other pages

		// Wrap it all up in a nice InstanceConfiguration object for convenience
		return new InstanceConfiguration(configJSON);

	}

}
