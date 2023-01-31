package org.canvacord.setup;

import org.canvacord.gui.TextPrompt;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.util.Globals;
import org.canvacord.util.string.StringUtils;

import javax.swing.*;
import java.util.Optional;

public class TokenSetupWizard extends CanvaCordWizard {

	private JTextField urlField;

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

		WizardCard urlCard = new WizardCard(true);
		urlCard.setLayout(null);

		JLabel boxLabel = new JLabel("Enter your institution's Canvas URL:");
		boxLabel.setFont(Globals.WIZARD_LABEL_FONT);

		urlField = new JTextField(50);
		TextPrompt urlPrompt = new TextPrompt("https://school.instructure.com", urlField);

		boxLabel.setBounds(30, 60, 200, 20);
		urlField.setBounds(220, 60, 300, 20);

		urlCard.add(boxLabel);
		urlCard.add(urlField);

		urlCard.setNavigator(Optional::empty);

		return urlCard;

	}

}
