package org.canvacord.gui.wizard.cards.instance;

import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
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

		int buttonX = 508;
		int buttonY = 134;
		int buttonSize = 48;
		int buttonSpacing = 20;

		JButton newRoleButton = new JButton(new ImageIcon("resources/new_icon.png"));
		newRoleButton.setBounds(buttonX, buttonY, buttonSize, buttonSize);
		contentPanel.add(newRoleButton);

		JButton editRoleButton = new JButton(new ImageIcon("resources/edit_icon_wip.png"));
		editRoleButton.setBounds(buttonX, buttonY + buttonSize + buttonSpacing, buttonSize, buttonSize);
		contentPanel.add(editRoleButton);

		JButton deleteRoleButton = new JButton(new ImageIcon("resources/delete_icon_non_beveled.png"));
		deleteRoleButton.setBounds(buttonX, buttonY + (buttonSize + buttonSpacing) * 2, buttonSize, buttonSize);
		contentPanel.add(deleteRoleButton);


		// TODO



	}

	@Override
	protected void initLogic() {
		// TODO
	}
}
