package org.canvacord.gui.component;

import net.miginfocom.swing.MigLayout;
import org.canvacord.event.CanvaCordEvent;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import java.awt.*;

public class InstanceDetailsPanel extends JPanel {

	private final Instance instance;

	public InstanceDetailsPanel(Instance instance) {
		this.instance = instance;
		buildGUI();
	}

	private void buildGUI() {

		// empty panel for no selection
		if (instance == null) {
			return;
		}

		setLayout(new MigLayout("", "[grow, center]", "[][][][][]"));

		JLabel nameLabel = new JLabel(instance.getName());
		nameLabel.setFont(CanvaCordFonts.HEADER_FONT);
		add(nameLabel, "cell 0 0");

		JLabel descriptionLabel = new JLabel(
				String.format(
						"""
						<html>
						Course ID: %s
						<br/>
						Server ID: %d
						<br/>
						<br/>
						Configured Roles: %d
						<br/>
						Configured Notifications: %d
						<br/>
						<br/>
						Has Syllabus: %s
						<br/>
						No. of Textbooks: %d
						</html>
						""",
						instance.getCourseID(),
						instance.getServerID(),
						instance.getConfiguredRoles().size(),
						instance.getConfiguredNotifications().size(),
						instance.hasSyllabus() ? "Yes" : "No",
						instance.getTextbooks().size()
				)
		);
		descriptionLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);;
		add(descriptionLabel, "cell 0 2");

		JButton deleteButton = new JButton("Delete Instance");
		deleteButton.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		deleteButton.setForeground(Color.RED);
		add(deleteButton, "cell 0 4");

		deleteButton.addActionListener(event -> {

			if (UserInput.askToConfirm("Are you sure you want to delete this\nInstance? This action cannot be undone.", "Confirm Deletion")) {
				InstanceManager.deleteInstance(instance);
				CanvaCordEvent.newEvent(CanvaCordEvent.Type.INSTANCE_DELETED, instance);
			}

		});

	}

}
