package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.discord.commands.CommandDescriptor;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommandToggleCard extends InstanceConfigCard {

	private List<CommandRecord> commands;
	private List<JCheckBox> checkBoxes;

	public CommandToggleCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Enable/Disable Commands");
	}

	@Override
	protected void buildGUI() {

		// Use an absolute layout for this one as well
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		// build list of commands that can be toggled
		commands = new ArrayList<>();
		addCommandRecord("Syllabus", true, null);
		addCommandRecord("Textbooks", true, null);
		addCommandRecord("Assignments List", true, null);
		addCommandRecord("Assignments Search", true, null);
		addCommandRecord("Assignments Active", true, null);
		addCommandRecord("Assignment Details", true, null);
		addCommandRecord("Announcements List", true, null);
		addCommandRecord("Announcements Search", true, null);
		addCommandRecord("Announcement Details", true, null);
		addCommandRecord("Remind Me", false, null);

		// store checkboxes to fetch their states later
		checkBoxes = new ArrayList<>();

		// positioning
		final int componentX = 20;
		final int cardLabelY = 4;
		final int cardLabelHeight = 50;

		final int checkBoxStartY = cardLabelY + cardLabelHeight + 30;
		final int checkBoxWidth = 160;
		final int checkBoxHeight = 24;
		final int verticalSpacing = 8;
		final int horizontalSpacing = 16;

		final int columnHeight = 250;
		final int rowsPerColumn = columnHeight / (checkBoxHeight + verticalSpacing);

		// Describe this card
		JLabel cardLabel = new JLabel(
				"""
					<html>Finally, choose which commands you'd like to make available to the
					users in your Discord server.</html>"""
		);
		cardLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		cardLabel.setBounds(componentX, cardLabelY, WIDTH - componentX * 3, cardLabelHeight);
		contentPanel.add(cardLabel);

		// Add all command records!
		for (int i = 0; i < commands.size(); i++) {

			CommandRecord commandRecord = commands.get(i);
			JCheckBox checkBox = new JCheckBox(commandRecord.name);
			checkBox.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
			checkBox.setBounds(componentX + i / rowsPerColumn * (checkBoxWidth + horizontalSpacing), checkBoxStartY + i % rowsPerColumn * (checkBoxHeight + verticalSpacing), checkBoxWidth, checkBoxHeight);
			checkBox.setSelected(commandRecord.defaultState);
			contentPanel.add(checkBox);

			checkBoxes.add(checkBox);

		}

	}

	@Override
	protected void initLogic() {
		// Nothing here, actually.
	}

	@Override
	protected void prefillGUI(Instance instanceToEdit) {
		// TODO Andrew
	}

	private void addCommandRecord(String name, boolean defaultState, CommandDescriptor descriptor) {
		commands.add(new CommandRecord(name, defaultState));
	}

	public record CommandRecord(String name, boolean defaultState) {}

	public List<CommandRecord> getCommandStates() {
		List<CommandRecord> result = new ArrayList<>();
		for (JCheckBox checkBox : checkBoxes) {
			result.add(new CommandRecord(checkBox.getText().toLowerCase().replaceAll(" ", "_"), checkBox.isSelected()));
		}
		return result;
	}

}
