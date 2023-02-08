package org.canvacord.setup;

import net.miginfocom.swing.MigLayout;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.discord.DiscordBot;
import org.canvacord.gui.BooleanTask;
import org.canvacord.gui.DangerousProgressBar;
import org.canvacord.gui.TextPrompt;
import org.canvacord.gui.VerifyBackgroundTask;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.persist.ConfigManager;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.string.StringUtils;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Optional;

public class TokenSetupWizard extends CanvaCordWizard {

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

	public TokenSetupWizard() {
		super("Configure CanvaCord");
	}

	@Override
	public void initCards() {

		// Card for adding the institution's Canvas URL and Canvas/Discord API tokens
		registerCard(buildMainCard());

		// Prepare the interactive GUI elements for user interactions
		initLogic();

		setFinishTask(() -> {

			if (!completedSuccessfully()) {
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
	public boolean completedSuccessfully() {

		// Verify the URL string
		if (!StringUtils.isURL(urlField.getText()))
			return false;

		// If the URL is valid, check the verification status
		return verifiedCanvasToken && verifiedDiscordToken;
	}

	private WizardCard buildMainCard() {

		WizardCard mainCard = new WizardCard("main", true);
		mainCard.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		// ================================ INPUT PANEL ================================
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(null);
		inputPanel.setMinimumSize(new Dimension(mainCard.getPreferredSize().width, 200));

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
		urlLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);

		urlField = new JTextField(50);
		TextPrompt urlPrompt = new TextPrompt("https://school.instructure.com", urlField);
		urlPrompt.setForeground(Color.GRAY);

		urlLabel.setBounds(labelX, urlY, 200, labelHeight);
		urlField.setBounds(fieldX, urlY, fieldWidth, fieldHeight);

		inputPanel.add(urlLabel);
		inputPanel.add(urlField);
		// ================ USER ID INPUT ================
		JLabel idLabel = new JLabel("Enter your Canvas User ID:");
		idLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);

		idField = new JTextField(8);
		TextPrompt idPrompt = new TextPrompt("12345", idField);
		idPrompt.setForeground(Color.GRAY);

		idLabel.setBounds(labelX, idY, 200, labelHeight);
		idField.setBounds(fieldX, idY,  fieldWidth, fieldHeight);

		inputPanel.add(idLabel);
		inputPanel.add(idField);
		// ================ CANVAS API TOKEN INPUT ================
		JLabel canvasTokenLabel = new JLabel("Enter your Canvas Access Token:");
		canvasTokenLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);

		canvasTokenField = new JTextField(100);
		TextPrompt canvasTokenPrompt = new TextPrompt("Token...", canvasTokenField);
		canvasTokenPrompt.setForeground(Color.GRAY);

		canvasTokenLabel.setBounds(labelX, canvasY, 200, labelHeight);
		canvasTokenField.setBounds(fieldX, canvasY, fieldWidth, fieldHeight);

		inputPanel.add(canvasTokenLabel);
		inputPanel.add(canvasTokenField);
		// ================ DISCORD TOKEN INPUT ================
		JLabel discordTokenLabel = new JLabel("Enter your Discord Bot Token:");
		discordTokenLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);

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
		verifyPanel.setMinimumSize(new Dimension(mainCard.getPreferredSize().width, 200));

		verifyPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Verification", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		// ================ VERIFY BUTTON ================
		verifyButton = new JButton("Verify");
		verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(Box.createVerticalStrut(12));
		verifyPanel.add(verifyButton);
		verifyPanel.add(Box.createVerticalStrut(12));
		// ================ CANVAS TOKEN VERIFY ================
		canvasTokenVerifyLabel = new JLabel("Canvas Token: Unverified");
		canvasTokenVerifyLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);
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
		discordTokenVerifyLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);
		discordTokenVerifyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		discordProgressBar = new DangerousProgressBar(0, 100);
		discordProgressBar.setMaximumSize(new Dimension(500, 16));
		discordProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(discordTokenVerifyLabel);
		verifyPanel.add(Box.createVerticalStrut(4));
		verifyPanel.add(discordProgressBar);
		// ================================ NO NEXT SCREEN ================================
		mainCard.setNavigator(Optional::empty);
		// ================================ ADD PANELS TO THE CARD ================================
		mainCard.add(inputPanel, "cell 0 0, growx, growy");
		mainCard.add(verifyPanel, "cell 0 1, growx, growy");

		return mainCard;

	}

	private void initLogic() {

		// any changes to the fields un-verify their contents
		DocumentListener canvasInfoEditListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				verifiedCanvasToken = false;
				canvasProgressBar.setValue(0);
				canvasTokenVerifyLabel.setText("Canvas Token: Unverified");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				verifiedCanvasToken = false;
				canvasProgressBar.setValue(0);
				canvasTokenVerifyLabel.setText("Canvas Token: Unverified");
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				verifiedCanvasToken = false;
				canvasProgressBar.setValue(0);
				canvasTokenVerifyLabel.setText("Canvas Token: Unverified");
			}
		};

		DocumentListener discordInfoEditListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				verifiedDiscordToken = false;
				discordProgressBar.setValue(0);
				discordTokenVerifyLabel.setText("Discord Token: Unverified");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				verifiedDiscordToken = false;
				discordProgressBar.setValue(0);
				discordTokenVerifyLabel.setText("Discord Token: Unverified");
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				verifiedDiscordToken = false;
				discordProgressBar.setValue(0);
				discordTokenVerifyLabel.setText("Discord Token: Unverified");
			}
		};

		urlField.getDocument().addDocumentListener(canvasInfoEditListener);
		idField.getDocument().addDocumentListener(canvasInfoEditListener);
		canvasTokenField.getDocument().addDocumentListener(canvasInfoEditListener);

		discordTokenField.getDocument().addDocumentListener(discordInfoEditListener);

		// react to the user clicking on the Verify button
		verifyButton.addActionListener(event -> {

			// ================ CANVAS TOKEN VERIFICATION ================
			if (!verifiedCanvasToken) {
				canvasProgressBar.setValue(0);
				canvasProgressBar.setIndeterminate(true);

				BooleanTask canvasVerify = () -> CanvasApi.testCanvasInfo(urlField.getText(), idField.getText(), canvasTokenField.getText());
				VerifyBackgroundTask verifyCanvasTokenTask = new VerifyBackgroundTask(this, canvasVerify, CANVAS_VERIFY);
				verifyCanvasTokenTask.execute();

				canvasTokenVerifyLabel.setText("Canvas Token: Verifying...");
			}

			// ================ DISCORD TOKEN VERIFICATION ================
			if (!verifiedDiscordToken) {
				discordProgressBar.setValue(0);
				discordProgressBar.setIndeterminate(true);

				BooleanTask discordVerify = () -> DiscordBot.testTokenString(discordTokenField.getText());
				VerifyBackgroundTask verifyDiscordTokenTask = new VerifyBackgroundTask(this, discordVerify, DISCORD_VERIFY);
				verifyDiscordTokenTask.execute();

				discordTokenVerifyLabel.setText("Discord Token: Verifying...");
			}

		});

	}

	private static final int CANVAS_VERIFY = 0;
	private static final int DISCORD_VERIFY = 1;

	public void updateVerifyTask(int typeCode, boolean verified) {

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

}
