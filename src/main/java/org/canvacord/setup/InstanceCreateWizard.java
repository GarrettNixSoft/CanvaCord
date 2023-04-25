package org.canvacord.setup;

import org.canvacord.canvas.TextbookInfo;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.cards.instance.*;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceConfiguration;
import org.canvacord.util.Globals;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.time.Profiler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * The InstanceCreateWizard implements the process of creating a new CanvaCord
 * instance.
 */
public class InstanceCreateWizard extends CanvaCordWizard {

	private InstanceSetupWelcomeCard startingCard;
	private CourseAndServerCard courseAndServerCard;
	private NameIconCard basicConfigCard;
	private InstanceCanvasFetchCard canvasFetchCard;
	private RoleCreateCard roleCreateCard;
	private NotificationCreateCard notificationCreateCard;
	private SyllabusCard syllabusCard;
	private TextbookCard textbookCard;
	private MeetingRemindersCard meetingRemindersCard;
	private MeetingMarkersCard meetingMarkersCard;
	private CommandToggleCard commandToggleCard;

	public InstanceCreateWizard() {
		super("Create Instance");
	}

	public InstanceCreateWizard(Instance instanceToEdit) {
		this();
		Globals.EDIT_INSTANCE_ID = instanceToEdit.getInstanceID();
		prefillCards(instanceToEdit);
		setCurrentCard(courseAndServerCard);
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
		basicConfigCard = new NameIconCard(this, "name_icon", false);

		// The fourth card is for setting up the Canvas fetching schedule
		canvasFetchCard = new InstanceCanvasFetchCard(this, "fetch_config", false);

		// The fifth card is for defining what roles this instance should use
		roleCreateCard = new RoleCreateCard(this, "role_config", false);

		// The sixth card is for defining what notifications should be sent
		notificationCreateCard = new NotificationCreateCard(this, "notification_config", false);

		// The seventh card is for adding the syllabus
		syllabusCard = new SyllabusCard(this, "syllabus_config", false);

		// The eighth card is for adding the textbook(s)
		textbookCard = new TextbookCard(this, "textbook_config", false);

		// The ninth card is for setting up class meeting reminders
		meetingRemindersCard = new MeetingRemindersCard(this, "meeting_reminder_config", false);

		// The tenth card is for setting up meeting markers
		meetingMarkersCard = new MeetingMarkersCard(this, "meeting_marker_config", false);

		// The eleventh card is for toggling command availability
		commandToggleCard = new CommandToggleCard(this, "command_toggle", true);

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

		canvasFetchCard.setOnNavigateTo(this::enableNext);

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
			//disableNext("<html>You must create at least one<br>Notification before continuing.</html>");
			notificationCreateCard.onNavigateTo();
		});

		// ================ SYLLABUS ================
		syllabusCard.setNavigator(() -> Optional.of(textbookCard));
		syllabusCard.setPreviousCard(notificationCreateCard);

		syllabusCard.setOnNavigateTo(() -> {
			enableNext();
		});

		// ================ TEXTBOOK(S) ================
		textbookCard.setNavigator(() -> Optional.of(meetingRemindersCard));
		textbookCard.setPreviousCard(syllabusCard);

		textbookCard.setOnNavigateTo(() -> textbookCard.onNavigateTo());

		// ================ MEETING REMINDERS ================
		meetingRemindersCard.setNavigator(() -> Optional.of(meetingMarkersCard));
		meetingRemindersCard.setPreviousCard(textbookCard);

		meetingRemindersCard.setOnNavigateTo(() -> meetingRemindersCard.onNavigateTo());

		// ================ MEETING MARKERS ================
		meetingMarkersCard.setNavigator(() -> Optional.of(commandToggleCard));
		meetingMarkersCard.setPreviousCard(meetingRemindersCard);

		meetingMarkersCard.setOnNavigateTo(() -> meetingMarkersCard.onNavigateTo());

		// ================ COMMAND TOGGLE ================
		commandToggleCard.setNavigator(Optional::empty);
		commandToggleCard.setPreviousCard(meetingMarkersCard);

		// Register the cards
		registerCard(startingCard);
		registerCard(courseAndServerCard);
		registerCard(basicConfigCard);
		registerCard(canvasFetchCard);
		registerCard(roleCreateCard);
		registerCard(notificationCreateCard);
		registerCard(syllabusCard);
		registerCard(textbookCard);
		registerCard(meetingRemindersCard);
		registerCard(meetingMarkersCard);
		registerCard(commandToggleCard);

		buildComplete();

	}

	/**
	 * Executes all the prefill commands from all the cards
	 * Andrew Bae
	 */
	private void prefillCards(Instance instanceToEdit) {
		// TODO Andrew

		long courseAndServerTime = Profiler.executeProfiled(() -> courseAndServerCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled courseAndServerCard in " + courseAndServerTime + "ms");

		long basicConfigTime = Profiler.executeProfiled(() -> basicConfigCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled basicConfigCard in " + basicConfigTime + "ms");

		long canvasFetchTime = Profiler.executeProfiled(() -> canvasFetchCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled canvasFetchCard in " + canvasFetchTime + "ms");

		long roleCreateTime = Profiler.executeProfiled(() -> roleCreateCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled roleCreateCard in " + roleCreateTime + "ms");

		long notificationCreateTime = Profiler.executeProfiled(() -> notificationCreateCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled notificationCreateCard in " + notificationCreateTime + "ms");

		long syllabusTime = Profiler.executeProfiled(() -> syllabusCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled syllabusCard in " + syllabusTime + "ms");

		long textbookTime = Profiler.executeProfiled(() -> textbookCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled textbookCard in " + textbookTime + "ms");

		long meetingRemindersTime = Profiler.executeProfiled(() -> meetingRemindersCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled meetingRemindersCard in " + meetingRemindersTime + "ms");

		long meetingMarkersTime = Profiler.executeProfiled(() -> meetingMarkersCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled meetingMarkersCard in " + meetingMarkersTime + "ms");

		long commandToggleTime = Profiler.executeProfiled(() -> commandToggleCard.prefillGUI(instanceToEdit));
		System.out.println("Prefilled commandToggleCard in " + commandToggleTime + "ms");
		enableNext();
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
		String instanceID = courseID + "-" + serverID;
		Globals.EDIT_INSTANCE_ID = instanceID;

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

		// Store a syllabus if one was added
		syllabusCard.getSyllabusFile().ifPresent(
				file -> {
					//Directory Checker
					File dirCheck = new File("./instances/" + instanceID);
					if(!dirCheck.exists())
						dirCheck.mkdirs();
					boolean success = FileUtil.copyTo(file, Paths.get("./instances/" + instanceID + "/syllabus.pdf").toAbsolutePath());
					if (!success) {
						UserInput.showWarningMessage("Failed to copy syllabus file.", "File Copy Error");
					}
					else {
						configJSON.put("has_syllabus", true);
					}
				}
		);

		// Make sure the instance directory exists so textbooks can be copied in
		File dirCheck = new File("instances/" + instanceID);
		if(!dirCheck.exists())
			if (!dirCheck.mkdirs()) {
				UserInput.showErrorMessage("Failed to create instance directory!", "I/O Error");
				return null;
			}

		// Store any textbooks
		List<TextbookInfo> textbooks = textbookCard.getTextbooks();
		JSONArray textbookFiles = new JSONArray();
		for (TextbookInfo bookInfo : textbooks) {
			TextbookInfo storedInfo = bookInfo.storeAndConvert();
			textbookFiles.put(storedInfo.textbookJSON());
		}
		configJSON.put("textbook_files", textbookFiles);

		// Configure class meeting reminders
		configJSON.put("do_meeting_reminders", meetingRemindersCard.doMeetingReminders());
		configJSON.put("create_reminders_role", meetingRemindersCard.createRole());
		configJSON.put("reminders_schedule", meetingRemindersCard.getReminderSchedule());
		configJSON.put("meeting_reminders_channel", meetingRemindersCard.getTargetChannelID());

		// store class schedule if necessary
		if (meetingRemindersCard.doMeetingReminders() || meetingMarkersCard.doMeetingMarkers()) {
			JSONArray classSchedule = new JSONArray();
			for (ClassMeeting meeting : meetingRemindersCard.getClassSchedule()) {
				classSchedule.put(meeting.getJSON());
			}
			configJSON.put("class_schedule", classSchedule);
		}
		else configJSON.put("class_schedule", new JSONArray());

		// Configure class meeting markers
		configJSON.put("do_meeting_markers", meetingMarkersCard.doMeetingMarkers());
		configJSON.put("meeting_markers_channel", meetingMarkersCard.getTargetChannelID());

		// configure command availability
		JSONObject commandsRecord = new JSONObject();
		for (CommandToggleCard.CommandRecord commandRecord : commandToggleCard.getCommandStates()) {
			commandsRecord.put(commandRecord.name(), commandRecord.defaultState());
		}
		configJSON.put("command_availability", commandsRecord);

		boolean doCustomReminders = commandsRecord.getBoolean("remindme");
		configJSON.put("do_custom_reminders", doCustomReminders);

		// Wrap it all up in a nice InstanceConfiguration object for convenience
		return new InstanceConfiguration(configJSON);

	}

}
