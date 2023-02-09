package org.canvacord.setup;

import net.miginfocom.swing.MigLayout;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.gui.*;
import org.canvacord.gui.wizard.BackgroundTaskWizard;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.util.input.UserInput;
import org.javacord.api.entity.server.Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

import java.util.List;

public class InstanceCreateWizard extends BackgroundTaskWizard<Boolean> {

	private JTextField courseInputField;
	private JTextField serverInputField;

	private JButton verifyButton;
	private JLabel courseVerifyLabel;
	private JLabel serverVerifyLabel;
	private DangerousProgressBar courseVerifyBar;
	private DangerousProgressBar serverVerifyBar;

	// verification status
	private boolean verifiedCanvasCourse = false;
	private boolean verifiedDiscordServer = false;

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
		WizardCard startingCard = buildStartingCard();

		// The second card is the Canvas course and Discord server setup page
		WizardCard courseServerCard = buildCourseAndServerCard();

		// The third card is the first page of configuration
		WizardCard configCard = buildConfigCard();

		// Configure the navigation connections
		startingCard.setNavigator(() -> Optional.of(courseServerCard));

		startingCard.setOnNavigateTo(this::enableNext);

		courseServerCard.setNavigator(() -> Optional.of(configCard));
		courseServerCard.setPreviousCard(startingCard);

		courseServerCard.setOnNavigateTo(() -> {
			if (!(verifiedCanvasCourse && verifiedDiscordServer)) {
				disableNext();
			}
		});

		configCard.setNavigator(Optional::empty);
		configCard.setPreviousCard(courseServerCard);

		configCard.setOnNavigateTo(this::enableNext);

		// Register the cards
		registerCard(startingCard);
		registerCard(courseServerCard);
		registerCard(configCard);
	}

	@Override
	public boolean completedSuccessfully() {
		// TODO
		return false;
	}

	private WizardCard buildStartingCard() {

		WizardCard startCard = new WizardCard("start", false);
		startCard.setLayout(new BorderLayout());

		JPanel startPanel = new JPanel();
		startPanel.setLayout(new BorderLayout());

		ImagePanel imagePanel = new ImagePanel("resources/setup_sidebar.png");
		startPanel.add(imagePanel, BorderLayout.WEST);
		imagePanel.setPreferredSize(new Dimension(200, 450));

		int textWidth = getMaximumSize().width - 50 - imagePanel.getPreferredSize().width;
		System.out.println("Text width = " + textWidth);

		int borderHeight = 50;
		int borderWidth = 20;

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setBorder(new EmptyBorder(borderHeight, borderWidth, borderHeight, borderWidth));
		textPanel.setMaximumSize(new Dimension(textWidth, startCard.getMaximumSize().height));

		JLabel newInstanceHeader = new JLabel("<html><body style='text-align: center'>New Instance Wizard</html>");
		newInstanceHeader.setFont(CanvaCordWizard.WIZARD_HEADER_FONT);
		newInstanceHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		textPanel.add(newInstanceHeader);

		textPanel.add(Box.createVerticalStrut(30));

		JTextArea welcomeText = new JTextArea();
		welcomeText.setEditable(false);
		welcomeText.setLineWrap(true);
		welcomeText.setWrapStyleWord(true);
		welcomeText.setBackground(new Color(0,0,0,0));
		welcomeText.setMaximumSize(new Dimension(textWidth, startCard.getMaximumSize().height));
		welcomeText.setText(
				"""
						Welcome to the CanvaCord Instance Creation Wizard. This process will guide you through the process of setting up a new CanvaCord instance.

						Before you get started, make sure you've set up your CanvaCord Discord Bot and added it to your target server. CanvaCord will verify that it has access to the server you specify as part of the setup process.""");
		welcomeText.setFont(CanvaCordWizard.getFont(13));
		textPanel.add(welcomeText);

		startPanel.add(textPanel, BorderLayout.CENTER);

		startCard.add(startPanel, BorderLayout.CENTER);

		return startCard;

	}

	private WizardCard buildCourseAndServerCard() {

		// ================ MAIN CARD ================
		WizardCard courseAndServerCard = new WizardCard("course_server", false);

		// ================ GUI SUB-PANEL ================
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new BorderLayout());

		// ================ HEADER ================
		ImagePanel topBar = new ImagePanel("resources/setup_topbar.png");
		topBar.setLayout(new BorderLayout());
		topBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		topBar.setPreferredSize(new Dimension(courseAndServerCard.getMaximumSize().width, 80));
		cardPanel.add(topBar, BorderLayout.NORTH);

		JLabel cardHeader = new JLabel("Set Course and Server");
		cardHeader.setFont(CanvaCordWizard.WIZARD_HEADER_FONT);
		topBar.add(cardHeader, BorderLayout.WEST);

		// ================ MAIN CONTENT ================
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		int subPanelHeight = 180;

		// ================ INPUT PANEL ================
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(null);
		inputPanel.setMinimumSize(new Dimension(courseAndServerCard.getPreferredSize().width, subPanelHeight - 40));

		inputPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(160, 160, 160)), "Course and Server Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));

		// ================ POSITIONING ELEMENTS ================
		int labelX = 60;
		int fieldX = labelX + 160;

		int labelHeight = 20;

		int fieldWidth = 200;
		int fieldHeight = 20;

		int fieldSpacing = 40;

		int canvasY = 45;
		int discordY = canvasY + fieldSpacing;

		int verifySpacing = 6;

		// ================ COURSE ID INPUT ================
		JLabel courseInputLabel = new JLabel("Enter Canvas Course ID:");
		courseInputLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);

		courseInputField = new JTextField(5);
		TextPrompt courseInputPrompt = new TextPrompt("12345", courseInputField);
		courseInputPrompt.setForeground(Color.GRAY);

		courseInputLabel.setBounds(labelX, canvasY, 200, labelHeight);
		courseInputField.setBounds(fieldX, canvasY, fieldWidth, fieldHeight);

		inputPanel.add(courseInputLabel);
		inputPanel.add(courseInputField);
		// ================ SERVER ID INPUT ================
		JLabel serverInputLabel = new JLabel("Enter Discord Server ID: ");
		serverInputLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);

		serverInputField = new JTextField(5);
		TextPrompt serverInputPrompt = new TextPrompt("123456789", serverInputField);
		serverInputPrompt.setForeground(Color.GRAY);

		serverInputLabel.setBounds(labelX, discordY, 200, labelHeight);
		serverInputField.setBounds(fieldX, discordY, fieldWidth, fieldHeight);

		inputPanel.add(serverInputLabel);
		inputPanel.add(serverInputField);

		// ================ VERIFICATION PANEL ================
		JPanel verifyPanel = new JPanel();
		verifyPanel.setLayout(new BoxLayout(verifyPanel, BoxLayout.Y_AXIS));
		verifyPanel.setMinimumSize(new Dimension(courseAndServerCard.getMaximumSize().width, subPanelHeight));

		verifyPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(160, 160, 160)), "Verification", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		verifyPanel.add(Box.createVerticalStrut(12));
		// ================ VERIFY BUTTON ================
		verifyButton = new JButton("Verify");
		verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(verifyButton);
		verifyPanel.add(Box.createVerticalStrut(20));
		// ================ COURSE VERIFICATION INDICATOR ================
		courseVerifyLabel = new JLabel("Course ID: Unverified");
		courseVerifyLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);
		courseVerifyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(courseVerifyLabel);
		verifyPanel.add(Box.createVerticalStrut(verifySpacing));

		courseVerifyBar = new DangerousProgressBar(0, 100);
		courseVerifyBar.setMaximumSize(new Dimension(500, 16));
		courseVerifyBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(courseVerifyBar);
		verifyPanel.add(Box.createVerticalStrut(verifySpacing));
		// ================ SERVER VERIFICATION INDICATOR ================
		serverVerifyLabel = new JLabel("Server ID: Unverified");
		serverVerifyLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);
		serverVerifyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(serverVerifyLabel);
		verifyPanel.add(Box.createVerticalStrut(verifySpacing));

		serverVerifyBar = new DangerousProgressBar(0, 100);
		serverVerifyBar.setMaximumSize(new Dimension(500, 16));
		serverVerifyBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(serverVerifyBar);
		// ================ ADD THE PANELS ================
		contentPanel.add(inputPanel, "cell 0 0, growx, growy");
		contentPanel.add(verifyPanel, "cell 0 1, growx, growy");

		cardPanel.add(contentPanel, BorderLayout.CENTER);

		courseAndServerCard.add(cardPanel);

		return courseAndServerCard;

	}

	private WizardCard buildConfigCard() {

		WizardCard configCard = new WizardCard("config_1", true); // TODO false

		// TODO
		configCard.add(new JLabel("TODO"));

		return configCard;

	}

	@Override
	protected void initLogic() {

		DocumentListener courseEditListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				verifiedCanvasCourse = false;
				courseVerifyBar.setFailed(false);
				courseVerifyBar.setValue(0);
				courseVerifyBar.repaint();
				courseVerifyLabel.setText("Course ID: Unverified");
				disableNext();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				verifiedCanvasCourse = false;
				courseVerifyBar.setFailed(false);
				courseVerifyBar.setValue(0);
				courseVerifyBar.repaint();
				courseVerifyLabel.setText("Course ID: Unverified");
				disableNext();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				verifiedCanvasCourse = false;
				courseVerifyBar.setFailed(false);
				courseVerifyBar.setValue(0);
				courseVerifyBar.repaint();
				courseVerifyLabel.setText("Course ID: Unverified");
				disableNext();
			}
		};

		DocumentListener serverEditListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				verifiedDiscordServer = false;
				serverVerifyBar.setFailed(false);
				serverVerifyBar.setValue(0);
				serverVerifyBar.repaint();
				serverVerifyLabel.setText("Course ID: Unverified");
				disableNext();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				verifiedDiscordServer = false;
				serverVerifyBar.setFailed(false);
				serverVerifyBar.setValue(0);
				serverVerifyBar.repaint();
				serverVerifyLabel.setText("Course ID: Unverified");
				disableNext();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				verifiedDiscordServer = false;
				serverVerifyBar.setFailed(false);
				serverVerifyBar.setValue(0);
				serverVerifyBar.repaint();
				serverVerifyLabel.setText("Course ID: Unverified");
				disableNext();
			}
		};

		courseInputField.getDocument().addDocumentListener(courseEditListener);
		serverInputField.getDocument().addDocumentListener(serverEditListener);

		verifyButton.addActionListener(event -> {

			// ================ CHECK FOR EMPTY FIELDS ================
			if (courseInputField.getText().isBlank()) {
				UserInput.showMessage("You must provide a course ID.", "Empty Field");
				return;
			}

			if (serverInputField.getText().isBlank()) {
				UserInput.showMessage("You must provide a server ID.", "Empty Field");
				return;
			}

			// ================ COURSE ID VERIFICATION ================
			if (!verifiedCanvasCourse) {

				courseVerifyBar.setValue(0);
				courseVerifyBar.setIndeterminate(true);

				BackgroundTask<Boolean> canvasVerify = () -> {
					try {
						CanvasApi.getInstance().getAssignments(courseInputField.getText());
						return true;
					}
					catch (IOException e) {
						return false;
					}
				};

				VerifyBackgroundTask verifyCanvasCourseTask = new VerifyBackgroundTask(this, canvasVerify, CANVAS_VERIFY);
				verifyCanvasCourseTask.execute();

				courseVerifyLabel.setText("Course ID: Verifying...");

			}

			// ================ SERVER ID VERIFICATION ================
			if (!verifiedDiscordServer) {

				serverVerifyBar.setValue(0);
				serverVerifyBar.setIndeterminate(true);

				BackgroundTask<Boolean> discordVerify = () -> {

					long serverID;

					try {
						serverID = Long.parseLong(serverInputField.getText());
					}
					catch (NumberFormatException e) {
						UserInput.showErrorMessage("Discord Server IDs should contain numbers only.", "Invalid Server ID");
						return false;
					}

					DiscordBot bot = DiscordBot.getBotInstance();
					bot.login();

					for (Server server : bot.getServerMemberships()) {
						if (server.getId() == serverID) {
							bot.disconnect();
							return true;
						}
					}

					bot.disconnect();

					UserInput.showErrorMessage("CanvaCord could not access the specified Discord server.\nPlease check your server ID and try again.", "Server Not Available");
					return false;
				};

				VerifyBackgroundTask verifyDiscordServerTask = new VerifyBackgroundTask(this, discordVerify, DISCORD_VERIFY);
				verifyDiscordServerTask.execute();

				serverVerifyLabel.setText("Server ID: Verifying...");

			}

		});

	}

	private static final int CANVAS_VERIFY = 0;
	private static final int DISCORD_VERIFY = 1;

	@Override
	public void updateTask(int typeCode, Boolean verified) {

		if (typeCode == CANVAS_VERIFY) {

			courseVerifyBar.setIndeterminate(false);

			if (verified) {
				courseVerifyBar.setValue(100);
				courseVerifyBar.setFailed(false);
				courseVerifyLabel.setText("Course ID: Verified!");
				verifiedCanvasCourse = true;
			}
			else {
				courseVerifyBar.setFailed(true);
				UserInput.showErrorMessage("Canvas Course ID verification failed.", "Bad Course ID");
				courseVerifyLabel.setText("Course ID: Verification Failed");
			}

		}
		else if (typeCode == DISCORD_VERIFY) {

			serverVerifyBar.setIndeterminate(false);

			if (verified) {
				serverVerifyBar.setValue(100);
				serverVerifyBar.setFailed(false);
				serverVerifyLabel.setText("Server ID: Verified!");
				verifiedDiscordServer = true;
			}
			else {
				serverVerifyBar.setFailed(true);
				UserInput.showErrorMessage("Discord Server ID verification failed.", "Bad Server ID");
				serverVerifyLabel.setText("Server ID: Verification Failed");
			}

		}

		if (verifiedCanvasCourse && verifiedDiscordServer) {
			setNextButtonEnabled(true);
			setNextButtonTooltip(null);
		}

	}
}
