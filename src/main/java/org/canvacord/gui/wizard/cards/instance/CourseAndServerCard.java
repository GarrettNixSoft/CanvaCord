package org.canvacord.gui.wizard.cards.instance;

import edu.ksu.canvas.model.Course;
import net.miginfocom.swing.MigLayout;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.DangerousProgressBar;
import org.canvacord.gui.component.TextPrompt;
import org.canvacord.gui.dialog.ChooseCourseDialog;
import org.canvacord.gui.dialog.ChooseServerDialog;
import org.canvacord.gui.task.BackgroundTask;
import org.canvacord.gui.task.VerifyBackgroundTask;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.cards.BackgroundTaskCard;
import org.canvacord.instance.Instance;
import org.canvacord.util.input.UserInput;
import org.javacord.api.entity.server.Server;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

/**
 * The CourseAndServerCard is the page in the instance configuration wizard
 * in which the user enters the Canvas course ID and Discord server ID for
 * their instance.
 */
public class CourseAndServerCard extends InstanceConfigCard implements BackgroundTaskCard<Boolean> {

	private JTextField courseInputField;
	private JTextField serverInputField;

	private JButton verifyButton;
	private JLabel courseVerifyLabel;
	private JLabel serverVerifyLabel;
	private DangerousProgressBar courseVerifyBar;
	private DangerousProgressBar serverVerifyBar;

	private JButton coursePickButton;
	private JButton serverPickButton;

	// verification status
	private boolean verifiedCanvasCourse = false;
	private boolean verifiedDiscordServer = false;

	private String courseTitle;
	private String serverName;

	public CourseAndServerCard(CanvaCordWizard parent, String name) {
		super(parent, name, false, "Set Course and Server");
	}

	public boolean isVerifiedCanvasCourse() {
		return verifiedCanvasCourse;
	}

	public boolean isVerifiedDiscordServer() {
		return verifiedDiscordServer;
	}

	@Override
	protected void buildGUI() {

		contentPanel.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		int subPanelHeight = 180;

		// ================ INPUT PANEL ================
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(null);
		inputPanel.setMinimumSize(new Dimension(CanvaCordWizard.WIDTH - 20, subPanelHeight - 40));
		inputPanel.setMaximumSize(new Dimension(CanvaCordWizard.WIDTH - 20, subPanelHeight - 40));
		inputPanel.setPreferredSize(new Dimension(CanvaCordWizard.WIDTH - 20, subPanelHeight - 40));

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
		courseInputLabel.setFont(CanvaCordFonts.LABEL_FONT_SMALL);

		courseInputField = new JTextField(5);
		TextPrompt courseInputPrompt = new TextPrompt("12345", courseInputField);
		courseInputPrompt.setForeground(Color.GRAY);

		courseInputLabel.setBounds(labelX, canvasY, 200, labelHeight);
		courseInputField.setBounds(fieldX, canvasY, fieldWidth, fieldHeight);

		inputPanel.add(courseInputLabel);
		inputPanel.add(courseInputField);

		coursePickButton = new JButton("Choose");
		coursePickButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		coursePickButton.setMargin(new Insets(2,2,2,2));
		coursePickButton.setBounds(fieldX + fieldWidth + 20, canvasY, 64, 24);
		inputPanel.add(coursePickButton);

		// ================ SERVER ID INPUT ================
		JLabel serverInputLabel = new JLabel("Enter Discord Server ID: ");
		serverInputLabel.setFont(CanvaCordFonts.LABEL_FONT_SMALL);

		serverInputField = new JTextField(5);
		TextPrompt serverInputPrompt = new TextPrompt("123456789", serverInputField);
		serverInputPrompt.setForeground(Color.GRAY);

		serverInputLabel.setBounds(labelX, discordY, 200, labelHeight);
		serverInputField.setBounds(fieldX, discordY, fieldWidth, fieldHeight);

		inputPanel.add(serverInputLabel);
		inputPanel.add(serverInputField);

		serverPickButton = new JButton("Choose");
		serverPickButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		serverPickButton.setMargin(new Insets(2,2,2,2));
		serverPickButton.setBounds(fieldX + fieldWidth + 20, discordY, 64, 24);
		inputPanel.add(serverPickButton);

		// ================ VERIFICATION PANEL ================
		JPanel verifyPanel = new JPanel();
		verifyPanel.setLayout(new BoxLayout(verifyPanel, BoxLayout.Y_AXIS));
		verifyPanel.setMinimumSize(new Dimension(CanvaCordWizard.WIDTH - 20, subPanelHeight));
		verifyPanel.setMaximumSize(new Dimension(CanvaCordWizard.WIDTH - 20, subPanelHeight));

		verifyPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(160, 160, 160)), "Verification", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		verifyPanel.add(Box.createVerticalStrut(12));
		// ================ VERIFY BUTTON ================
		verifyButton = new JButton("Verify");
		verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(verifyButton);
		verifyPanel.add(Box.createVerticalStrut(20));
		// ================ COURSE VERIFICATION INDICATOR ================
		courseVerifyLabel = new JLabel("Course ID: Unverified");
		courseVerifyLabel.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
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
		serverVerifyLabel.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
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

	}

	private static final int CANVAS_VERIFY = 0;
	private static final int DISCORD_VERIFY = 1;

	@Override
	protected void initLogic() {

		// Build document listeners to remove verification status for any verified inputs that get changed
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

		coursePickButton.addActionListener(event -> {
			ChooseCourseDialog.chooseCourse().ifPresent(
					course -> {
						courseInputField.setText("" + course.getId());
					}
			);
		});

		serverPickButton.addActionListener(event -> {
			ChooseServerDialog.chooseServer().ifPresent(
					server -> {
						serverInputField.setText("" + server.getId());
					}
			);
		});

		// Add logic to the button being pressed
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
						Optional<Course> course = CanvasApi.getInstance().getCourse(courseInputField.getText());
						course.ifPresent(c -> setCourseTitle(c.getName()));
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
							setServerName(server.getName());
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

	@Override
	/**
	 * Prefills the Course and Server Card
	 * Andrew Bae
	 */
	public void prefillGUI(Instance instanceToEdit) {
		// TODO Andrew
		courseInputField.setText(instanceToEdit.getCourseID());
		serverInputField.setText("" + instanceToEdit.getServerID());
		setCourseTitle(instanceToEdit.getCourseTitle());
		setServerName(instanceToEdit.getServerName());
		courseVerifyBar.setValue(100);
		courseVerifyBar.setFailed(false);
		courseVerifyLabel.setText("Course ID: Verified!");
		verifiedCanvasCourse = true;
		serverVerifyBar.setValue(100);
		serverVerifyBar.setFailed(false);
		serverVerifyLabel.setText("Server ID: Verified!");
		verifiedDiscordServer = true;

	}

	private void disableNext() {
		getParentWizard().setNextButtonEnabled(false);
		getParentWizard().setNextButtonTooltip("<html>You must verify your Course ID and<br>Server ID before continuing.</html>");
	}

	@Override
	public void updateTask(int typeCode, Boolean verified) {

		// This method is called by the verification tasks when they finish, and is used
		// to update the GUI elements to display the result of those tasks

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
				UserInput.showErrorMessage("Discord Server ID verification failed.\n(Has your bot joined the server yet?)", "Bad Server ID");
				serverVerifyLabel.setText("Server ID: Verification Failed");
			}

		}

		if (verifiedCanvasCourse && verifiedDiscordServer) {
			getParentWizard().setNextButtonEnabled(true);
			getParentWizard().setNextButtonTooltip(null);
		}
	}

	private void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	private void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getCourseID() {
		if (!verifiedCanvasCourse)
			throw new CanvaCordException("Unverified course ID requested!");
		else return courseInputField.getText();
	}

	public long getServerID() {
		if (!verifiedDiscordServer)
			throw new CanvaCordException("Unverified server ID requested!");
		else return Long.parseLong(serverInputField.getText());
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public String getServerName() {
		return serverName;
	}
}
