package org.canvacord.setup;

import net.miginfocom.swing.MigLayout;
import org.canvacord.gui.DangerousProgressBar;
import org.canvacord.gui.TextPrompt;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.util.string.StringUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Optional;

public class TokenSetupWizard extends CanvaCordWizard {

	private JTextField urlField;
	private JTextField canvasTokenField;
	private JTextField discordTokenField;

	private DangerousProgressBar canvasProgressBar;
	private DangerousProgressBar discordProgressBar;

	public TokenSetupWizard() {
		super("Configure CanvaCord");
	}

	@Override
	public void initCards() {

		// Card for adding the institution's Canvas URL and Canvas/Discord API tokens
		registerCard(buildMainCard());

	}

	@Override
	public boolean completedSuccessfully() {

		if (!StringUtils.isURL(urlField.getText()))
			return false;

		// TODO

		return true;
	}

	private WizardCard buildMainCard() {

		WizardCard mainCard = new WizardCard(true);
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

		int urlY = 45;
		int canvasY = 85;
		int discordY = 125;

		// ================ INSTITUTION URL INPUT ================
		JLabel boxLabel = new JLabel("Enter your institution's Canvas URL:");
		boxLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);

		urlField = new JTextField(50);
		TextPrompt urlPrompt = new TextPrompt("https://school.instructure.com", urlField);
		urlPrompt.setForeground(Color.GRAY);

		boxLabel.setBounds(labelX, urlY, 200, labelHeight);
		urlField.setBounds(fieldX, urlY, fieldWidth, fieldHeight);

		inputPanel.add(boxLabel);
		inputPanel.add(urlField);
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
		JButton verifyButton = new JButton("Verify");
		verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(Box.createVerticalStrut(12));
		verifyPanel.add(verifyButton);
		verifyPanel.add(Box.createVerticalStrut(12));
		// ================ CANVAS TOKEN VERIFY ================
		JLabel canvasTokenVerifyLabel = new JLabel("Canvas Token");
		canvasTokenVerifyLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);
		canvasTokenVerifyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		canvasProgressBar = new DangerousProgressBar();
		canvasProgressBar.setMaximumSize(new Dimension(500, 16));
		canvasProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		verifyPanel.add(canvasTokenVerifyLabel);
		verifyPanel.add(Box.createVerticalStrut(4));
		verifyPanel.add(canvasProgressBar);

		verifyPanel.add(Box.createVerticalStrut(12));
		// ================ DISCORD TOKEN VERIFY ================
		JLabel discordTokenVerifyLabel = new JLabel("Discord Token");
		discordTokenVerifyLabel.setFont(CanvaCordWizard.WIZARD_LABEL_FONT);
		discordTokenVerifyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		discordProgressBar = new DangerousProgressBar();
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

}
