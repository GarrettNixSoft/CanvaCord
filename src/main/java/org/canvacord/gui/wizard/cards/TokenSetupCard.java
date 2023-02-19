package org.canvacord.gui.wizard.cards;

import net.miginfocom.swing.MigLayout;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.task.BackgroundTask;
import org.canvacord.gui.component.DangerousProgressBar;
import org.canvacord.gui.component.TextPrompt;
import org.canvacord.gui.task.VerifyBackgroundTask;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.persist.ConfigManager;
import org.canvacord.util.input.UserInput;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Optional;

/**
 * The TokenSetupCard is the card which handles the initial setup of CanvaCord, in which
 * the user enters their login information which CanvaCord will use to access Canvas and
 * Discord on their behalf.
 */
public class TokenSetupCard extends WizardCard implements BackgroundTaskCard<Boolean> {

	private JTextField urlField;
	private JTextField idField;
	private JTextField canvasTokenField;
	private JTextField discordTokenField;

	private JButton verifyButton;
	private JLabel canvasTokenVerifyLabel;
	private DangerousProgressBar canvasProgressBar;
	private JLabel discordTokenVerifyLabel;
	private DangerousProgressBar discordProgressBar;

	private boolean verifiedCanvasToken;
	private boolean verifiedDiscordToken;

	public TokenSetupCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard);
		buildGUI();
		initLogic();
		initFinishTask();
	}

	private void initFinishTask() {

		getParentWizard().setFinishTask(() -> {

			if (!getParentWizard().completedSuccessfully()) {
				return UserInput.askToConfirm("You haven't verified your API tokens yet.\nIf you exit now, CanvaCord will not work\nuntil you verify your API tokens.\n\nDo you want to exit anyway?", "Unverified Tokens");
			}
			else {
				// verified tokens should be saved to disk
				JSONObject tokensJSON = new JSONObject();
				tokensJSON.put("url", urlField.getText());
				tokensJSON.put("id", idField.getText());
				tokensJSON.put("canvas_token", canvasTokenField.getText());
				tokensJSON.put("discord_token", discordTokenField.getText());
				if (!ConfigManager.writeTokenData(tokensJSON)) {
					UserInput.showErrorMessage("Something went wrong saving your tokens.", "File Write Error");
					return false;
				}
				// allow the wizard to close
				else return true;
			}

		});

	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		// ================================ INPUT PANEL ================================
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(null);
		inputPanel.setMinimumSize(new Dimension(getPreferredSize().width, 200));

		inputPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		// ================ POSITIONING ELEMENTS ================
		int labelX = 30;
		int fieldX = 220;

		int labelHeight = 20;

		int fieldWidth = 300;
		int fieldHeight = 20;

		int fieldSpacing = 40;

		int urlY = 45;
		int idY = urlY + fieldSpacing;
		int canvasY = idY + fieldSpacing;
		int discordY = canvasY + fieldSpacing;

		// ================ INSTITUTION URL INPUT ================
		JLabel urlLabel = new JLabel("Enter your institution's Canvas URL:");
		urlLabel.setFont(CanvaCordFonts.LABEL_FONT);

		urlField = new JTextField(50);
		TextPrompt urlPrompt = new TextPrompt("https://school.instructure.com", urlField);
		urlPrompt.setForeground(Color.GRAY);

		urlLabel.setBounds(labelX, urlY, 200, labelHeight);
		urlField.setBounds(fieldX, urlY, fieldWidth, fieldHeight);

		inputPanel.add(urlLabel);
		inputPanel.add(urlField);
		// ================ USER ID INPUT ================
		JLabel idLabel = new JLabel("Enter your Canvas User ID:");
		idLabel.setFont(CanvaCordFonts.LABEL_FONT);

		idField = new JTextField(8);
		TextPrompt idPrompt = new TextPrompt("12345", idField);
		idPrompt.setForeground(Color.GRAY);

		idLabel.setBounds(labelX, idY, 200, labelHeight);
		idField.setBounds(fieldX, idY,  fieldWidth, fieldHeight);

		inputPanel.add(idLabel);
		inputPanel.add(idField);
		// ================ CANVAS API TOKEN INPUT ================
		JLabel canvasTokenLabel = new JLabel("Enter your Canvas Access Token:");
		canvasTokenLabel.setFont(CanvaCordFonts.LABEL_FONT);

		canvasTokenField = new JTextField(100);
		TextPrompt canvasTokenPrompt = new TextPrompt("Token...", canvasTokenField);
		canvasTokenPrompt.setForeground(Color.GRAY);

		canvasTokenLabel.setBounds(labelX, canvasY, 200, labelHeight);
		canvasTokenField.setBounds(fieldX, canvasY, fieldWidth, fieldHeight);

		inputPanel.add(canvasTokenLabel);
		inputPanel.add(canvasTokenField);
		// ================ DISCORD TOKEN INPUT ================
		JLabel discordTokenLabel = new JLabel("Enter your Discord Bot Token:");
		discordTokenLabel.setFont(CanvaCordFonts.LABEL_FONT);

		discordTokenField = new JTextField(100);
		TextPrompt discordTokenPrompt = new TextPrompt("Token...", discordTokenField);
		discordTokenPrompt.setForeground(Color.GRAY);

		discordTokenLabel.setBounds(labelX, discordY, 200, labelHeight);
		discordTokenField.setBounds(fieldX, discordY, fieldWidth, fieldHeight);

		inputPanel.add(discordTokenLabel);
		inputPanel.add(discordTokenField);
		// ================================ VERIFY PANEL ================================
		JPanel verifyPanel = new JPanel();
		verifyPanel.setLayout(new BoxLayout(verifyPanel, BoxLayout.Y_AXIS));
		verifyPanel.setMinimumSize(new Dimension(getPreferredSize().width, 200));

		verifyPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Verification", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		// ================ VERIFY BUTTON ================
		verifyButton = new JButton("Verify");
		verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(Box.createVerticalStrut(12));
		verifyPanel.add(verifyButton);
		verifyPanel.add(Box.createVerticalStrut(12));
		// ================ CANVAS TOKEN VERIFY ================
		canvasTokenVerifyLabel = new JLabel("Canvas Token: Unverified");
		canvasTokenVerifyLabel.setFont(CanvaCordFonts.LABEL_FONT);
		canvasTokenVerifyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		canvasProgressBar = new DangerousProgressBar(0, 100);
		canvasProgressBar.setMaximumSize(new Dimension(500, 16));
		canvasProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(canvasTokenVerifyLabel);
		verifyPanel.add(Box.createVerticalStrut(4));
		verifyPanel.add(canvasProgressBar);

		verifyPanel.add(Box.createVerticalStrut(12));
		// ================ DISCORD TOKEN VERIFY ================
		discordTokenVerifyLabel = new JLabel("Discord Token: Unverified");
		discordTokenVerifyLabel.setFont(CanvaCordFonts.LABEL_FONT);
		discordTokenVerifyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		discordProgressBar = new DangerousProgressBar(0, 100);
		discordProgressBar.setMaximumSize(new Dimension(500, 16));
		discordProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(discordTokenVerifyLabel);
		verifyPanel.add(Box.createVerticalStrut(4));
		verifyPanel.add(discordProgressBar);

		// ================================ NO NEXT SCREEN ================================
		setNavigator(Optional::empty);
		// ================================ ADD PANELS TO THE CARD ================================
		add(inputPanel, "cell 0 0, growx, growy");
		add(verifyPanel, "cell 0 1, growx, growy");

	}

	@Override
	protected void initLogic() {

		// any changes to the fields un-verify their contents
		DocumentListener canvasInfoEditListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				verifiedCanvasToken = false;
				canvasProgressBar.setFailed(false);
				canvasProgressBar.setValue(0);
				canvasProgressBar.repaint();
				canvasTokenVerifyLabel.setText("Canvas Token: Unverified");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				verifiedCanvasToken = false;
				canvasProgressBar.setFailed(false);
				canvasProgressBar.setValue(0);
				canvasProgressBar.repaint();
				canvasTokenVerifyLabel.setText("Canvas Token: Unverified");
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				verifiedCanvasToken = false;
				canvasProgressBar.setFailed(false);
				canvasProgressBar.setValue(0);
				canvasProgressBar.repaint();
				canvasTokenVerifyLabel.setText("Canvas Token: Unverified");
			}
		};

		DocumentListener discordInfoEditListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				verifiedDiscordToken = false;
				discordProgressBar.setFailed(false);
				discordProgressBar.setValue(0);
				discordProgressBar.repaint();
				discordTokenVerifyLabel.setText("Discord Token: Unverified");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				verifiedDiscordToken = false;
				discordProgressBar.setFailed(false);
				discordProgressBar.setValue(0);
				discordProgressBar.repaint();
				discordTokenVerifyLabel.setText("Discord Token: Unverified");
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				verifiedDiscordToken = false;
				discordProgressBar.setFailed(false);
				discordProgressBar.setValue(0);
				discordProgressBar.repaint();
				discordTokenVerifyLabel.setText("Discord Token: Unverified");
			}
		};

		urlField.getDocument().addDocumentListener(canvasInfoEditListener);
		idField.getDocument().addDocumentListener(canvasInfoEditListener);
		canvasTokenField.getDocument().addDocumentListener(canvasInfoEditListener);

		discordTokenField.getDocument().addDocumentListener(discordInfoEditListener);

		// react to the user clicking on the Verify button
		verifyButton.addActionListener(event -> {

			// ================ CHECK FOR EMPTY FIELDS ================
			if (urlField.getText().isBlank()) {
				UserInput.showMessage("You must provide a URL.", "Empty Field");
				return;
			}

			if (idField.getText().isBlank()) {
				UserInput.showMessage("You must provide a user ID.", "Empty Field");
				return;
			}

			if (canvasTokenField.getText().isBlank()) {
				UserInput.showMessage("You must provide a Canvas Token.", "Empty Field");
				return;
			}

			if (discordTokenField.getText().isBlank()) {
				UserInput.showMessage("You must provide a Discord Token.", "Empty Field");
				return;
			}

			// ================ CANVAS TOKEN VERIFICATION ================
			if (!verifiedCanvasToken) {
				canvasProgressBar.setValue(0);
				canvasProgressBar.setIndeterminate(true);

				BackgroundTask<Boolean> canvasVerify = () -> CanvasApi.testCanvasInfo(urlField.getText(), idField.getText(), canvasTokenField.getText());
				VerifyBackgroundTask verifyCanvasTokenTask = new VerifyBackgroundTask(this, canvasVerify, CANVAS_VERIFY);
				verifyCanvasTokenTask.execute();

				canvasTokenVerifyLabel.setText("Canvas Token: Verifying...");
			}

			// ================ DISCORD TOKEN VERIFICATION ================
			if (!verifiedDiscordToken) {
				discordProgressBar.setValue(0);
				discordProgressBar.setIndeterminate(true);

				BackgroundTask<Boolean> discordVerify = () -> DiscordBot.testTokenString(discordTokenField.getText());
				VerifyBackgroundTask verifyDiscordTokenTask = new VerifyBackgroundTask(this, discordVerify, DISCORD_VERIFY);
				verifyDiscordTokenTask.execute();

				discordTokenVerifyLabel.setText("Discord Token: Verifying...");
			}

		});

	}

	private static final int CANVAS_VERIFY = 0;
	private static final int DISCORD_VERIFY = 1;

	@Override
	public void updateTask(int typeCode, Boolean verified) {

		// This method is called by the verification tasks when they finish, and is used
		// to update the GUI elements to display the result of those tasks

		if (typeCode == CANVAS_VERIFY) {

			canvasProgressBar.setIndeterminate(false);

			if (verified) {
				canvasProgressBar.setValue(100);
				canvasProgressBar.setFailed(false);
				canvasTokenVerifyLabel.setText("Canvas Token: Verified!");
				verifiedCanvasToken = true;
			}
			else {
				canvasProgressBar.setFailed(true);
				UserInput.showErrorMessage("Canvas token verification failed.", "Bad Canvas Token");
				canvasTokenVerifyLabel.setText("Canvas Token: Verification Failed");
			}

		}
		else if (typeCode == DISCORD_VERIFY) {

			discordProgressBar.setIndeterminate(false);

			if (verified) {
				discordProgressBar.setValue(100);
				discordProgressBar.setFailed(false);
				discordTokenVerifyLabel.setText("Discord Token: Verified!");
				verifiedDiscordToken = true;
			}
			else {
				discordProgressBar.setFailed(true);
				UserInput.showErrorMessage("Discord token verification failed.", "Bad Discord Token");
				discordTokenVerifyLabel.setText("Discord Token: Verification Failed");
			}

		}

	}

	public JTextField getUrlField() {
		return urlField;
	}

	public boolean isVerifiedCanvasToken() {
		return verifiedCanvasToken;
	}

	public boolean isVerifiedDiscordToken() {
		return verifiedDiscordToken;
	}
}
