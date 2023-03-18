package org.canvacord.setup;

import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.cards.instance.*;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceConfiguration;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
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
	private NotificationCreateCard notificationCreateCard;
	private SyllabusCard syllabusCard;
	private TextbookCard textbookCard;

	public InstanceCreateWizard() {
		super("Create Instance");
	}

	public InstanceCreateWizard(Instance instanceToEdit) {
		this();
		prefillCards(instanceToEdit);
	}

	private void disableNext(String message) {
		setNextButtonEnabled(false);
		setNextButtonTooltip(message);
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
		basicConfigCard = new InstanceBasicConfigCard(this, "name_icon", false);

		// The fourth card is for setting up the Canvas fetching schedule
		canvasFetchCard = new InstanceCanvasFetchCard(this, "fetch_config", false);

		// The fifth card is for defining what roles this instance should use
		roleCreateCard = new RoleCreateCard(this, "role_config", false);

		// The sixth card is for defining what notifications should be sent
		notificationCreateCard = new NotificationCreateCard(this, "notification_config", false);

		// The seventh card is for adding the syllabus
		syllabusCard = new SyllabusCard(this, "syllabus_config", false);

		// The eighth card is for adding the textbook(s)
		textbookCard = new TextbookCard(this, "textbook_config", true);

		// ================================ Configure the navigation connections ================================
		// ================ START ================
		startingCard.setNavigator(() -> Optional.of(courseAndServerCard));
		startingCard.setOnNavigateTo(this::enableNext);

		// ================ COURSE AND SERVER ================
		courseAndServerCard.setNavigator(() -> Optional.of(basicConfigCard));
		courseAndServerCard.setPreviousCard(startingCard);

		courseAndServerCard.setOnNavigateTo(() -> {
			if (!(courseAndServerCard.isVerifiedCanvasCourse() && courseAndServerCard.isVerifiedDiscordServer())) {
				disableNext("<html>You must verify your Course ID and<br>Server ID before continuing.</html>");
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
		roleCreateCard.setNavigator(() -> Optional.of(notificationCreateCard));
		roleCreateCard.setPreviousCard(canvasFetchCard);

		roleCreateCard.setOnNavigateTo(() -> {
			roleCreateCard.onNavigateTo();
		});

		// ================ NOTIFICATIONS ================
		notificationCreateCard.setNavigator(() -> Optional.of(syllabusCard));
		notificationCreateCard.setPreviousCard(roleCreateCard);

		notificationCreateCard.setOnNavigateTo(() -> {
			disableNext("<html>You must create at least one<br>Notification before continuing.</html>");
		});

		// ================ SYLLABUS ================
		syllabusCard.setNavigator(() -> Optional.of(textbookCard));
		syllabusCard.setPreviousCard(notificationCreateCard);

		syllabusCard.setOnNavigateTo(() -> {
			enableNext();
		});

		// ================ TEXTBOOK(S) ================
		textbookCard.setNavigator(Optional::empty);
		textbookCard.setPreviousCard(syllabusCard);

		// Register the cards

		registerCard(startingCard);
		registerCard(courseAndServerCard);
		registerCard(basicConfigCard);
		registerCard(canvasFetchCard);
		registerCard(roleCreateCard);
		registerCard(notificationCreateCard);
		registerCard(syllabusCard);
		registerCard(textbookCard);
	}

	private void prefillCards(Instance instanceToEdit) {
		// TODO Andrew
	}

	@Override
	public boolean completedSuccessfully() {

		// TODO

		// cancelling the wizard constitutes a failure
		if (isCancelled())
			return false;

		// failing to verify the course and server is also a failure
		if (!(courseAndServerCard.isVerifiedCanvasCourse() && courseAndServerCard.isVerifiedDiscordServer()))
			return false;

		// not adding any roles is a failure
		if (roleCreateCard.getRolesArray().length() < 1)
			return false;

		// not adding any notifications is a failure
		if (notificationCreateCard.getNotificationsArray().length() < 1)
			return false;

		// if none of the above apply, success
		return true;

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

		// Icon path is optional
		String iconPath = basicConfigCard.getIconPath();
		if (!iconPath.isBlank())
			if (FileUtil.isValidFile(iconPath, "png", "jpg", "jpeg"))
				configJSON.put("icon_path", iconPath);
			else
				UserInput.showMessage("Could not load the specified image.\nUsing default icon.", "Bad Icon Path");

		// Add the user's configured Canvas Fetch schedule
		JSONObject scheduleObject = canvasFetchCard.getScheduleJSON();
		configJSON.put("canvas_fetch_schedule", scheduleObject);

		// Generate roles and add them to the configuration
		JSONArray rolesArray = roleCreateCard.getRolesArray();
		configJSON.put("roles", rolesArray);

		// Generate notifications and add them to the configuration
		JSONArray notificationsArray = notificationCreateCard.getNotificationsArray();
		configJSON.put("notifications", notificationsArray);

		// TODO add more settings from other pages

		// Wrap it all up in a nice InstanceConfiguration object for convenience
		return new InstanceConfiguration(configJSON);

	}

}
