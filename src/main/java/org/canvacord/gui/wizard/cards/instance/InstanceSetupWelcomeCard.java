package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ImagePanel;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The InstanceSetupWelcomeCard is the first card (page) the user sees when
 * opening the InstanceCreateWizard. It informs them of some important
 * prerequisite steps that should be taken before continuing to the next step.
 */
public class InstanceSetupWelcomeCard extends WizardCard {

	public InstanceSetupWelcomeCard(CanvaCordWizard parent, String name) {
		super(parent, name, false);
		buildGUI();
		initLogic();
	}

	@Override
	protected void buildGUI() {

		setLayout(new BorderLayout());

		// ================ START SCREEN PANEL ================
		JPanel startPanel = new JPanel();
		startPanel.setLayout(new BorderLayout());

		// ================ SIDE BAR IMAGE ================
		ImagePanel imagePanel = ImagePanel.loadFromResources("setup_sidebar.png");
		startPanel.add(imagePanel, BorderLayout.WEST);
		imagePanel.setPreferredSize(new Dimension(200, 450));

		int textWidth = getMaximumSize().width - 50 - imagePanel.getPreferredSize().width;
		System.out.println("Text width = " + textWidth);

		int borderHeight = 50;
		int borderWidth = 20;

		// ================ PANEL FOR DISPLAYING TEXT ================
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setBorder(new EmptyBorder(borderHeight, borderWidth, borderHeight, borderWidth));
		textPanel.setMaximumSize(new Dimension(textWidth, getMaximumSize().height));

		JLabel newInstanceHeader = new JLabel("<html><body style='text-align: center'>New Instance Wizard</html>");
		newInstanceHeader.setFont(CanvaCordFonts.HEADER_FONT);
		newInstanceHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		textPanel.add(newInstanceHeader);

		textPanel.add(Box.createVerticalStrut(30));

		// ================ WELCOME TEXT AND INSTRUCTIONS ================
		JTextArea welcomeText = new JTextArea();
		welcomeText.setEditable(false);
		welcomeText.setLineWrap(true);
		welcomeText.setWrapStyleWord(true);
		welcomeText.setBackground(new Color(0,0,0,0));
		welcomeText.setMaximumSize(new Dimension(textWidth, getMaximumSize().height));
		welcomeText.setText(
				"""
						Welcome to the CanvaCord Instance Creation Wizard. This process will guide you through the process of setting up a new CanvaCord instance.

						Before you get started, make sure you've set up your CanvaCord Discord Bot and added it to your target server. CanvaCord will verify that it has access to the server you specify as part of the setup process.""");
		welcomeText.setFont(CanvaCordWizard.getFont(13));
		textPanel.add(welcomeText);

		startPanel.add(textPanel, BorderLayout.CENTER);

		add(startPanel, BorderLayout.CENTER);

	}

	@Override
	public void initLogic() {
		// no interactions here
	}
}
