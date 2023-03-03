package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.discord.CanvaCordRole;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoleCreateCard extends InstanceConfigCard {

	private List<CanvaCordRole> roles;

	public RoleCreateCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Set Up Roles");
	}

	@Override
	protected void buildGUI() {

		// Use an absolute layout for this one
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		JLabel description = new JLabel();
		description.setText(
				"""
				<html>Next, let's set up some Roles for your CanvaCord instance. These are the Roles that
				CanvaCord will create in Discord and use for sending notifications to users when new
				Canvas objects are found during the fetch stage. In the next step, you'll configure what
				notifications should be sent, when, and to which of the roles you create here.</html>
				"""
		);
		description.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		description.setBounds(20, 0, WizardCard.WIDTH - 40, 100);
		contentPanel.add(description);

		JScrollPane rolesPane = new JScrollPane();
		rolesPane.setBounds(20, 120, 450, 220);
		contentPanel.add(rolesPane);

		JList<CanvaCordRole> rolesList = new JList<>();
		rolesPane.getViewport().setView(rolesList);

		// TODO



	}

	@Override
	protected void initLogic() {
		// TODO
	}
}
