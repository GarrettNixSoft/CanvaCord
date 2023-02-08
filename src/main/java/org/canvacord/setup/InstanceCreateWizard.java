package org.canvacord.setup;

import org.canvacord.gui.ImagePanel;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.checkerframework.checker.units.qual.C;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class InstanceCreateWizard extends CanvaCordWizard {

	public InstanceCreateWizard() {
		super("Create Instance");
	}

	@Override
	protected void initCards() {

		// The first card is the welcome page
		WizardCard startingCard = buildStartingCard();

		// The second card is the Canvas course and Discord server setup page
		WizardCard courseServerCard = buildCourseAndServerCard();

		// Configure the navigation connections
		startingCard.setNavigator(() -> Optional.of(courseServerCard));

		courseServerCard.setNavigator(Optional::empty);
		courseServerCard.setPreviousCard(startingCard);

		// Register the cards
		registerCard(startingCard);
		registerCard(courseServerCard);
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

		WizardCard courseAndServerCard = new WizardCard("course_server", true); // TODO false

		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new BorderLayout());

		ImagePanel topBar = new ImagePanel("resources/setup_topbar.png");
		topBar.setLayout(new BorderLayout());
		topBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		topBar.setPreferredSize(new Dimension(courseAndServerCard.getMaximumSize().width, 80));
		cardPanel.add(topBar, BorderLayout.NORTH);

		JLabel cardHeader = new JLabel("Set Course and Server");
		cardHeader.setFont(CanvaCordWizard.WIZARD_HEADER_FONT);
		topBar.add(cardHeader, BorderLayout.WEST);

		courseAndServerCard.add(cardPanel);

		return courseAndServerCard;

	}
}
