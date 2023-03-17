package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.canvas.TextbookInfo;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;

import javax.swing.*;
import java.awt.*;

import java.util.List;

public class TextbookCard extends InstanceConfigCard {

	private List<TextbookInfo> textbooks;
	private JList<TextbookInfo> textbooksList;

	private JButton addButton;
	private JButton scanButton;

	public TextbookCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Add Textbooks");
	}

	@Override
	protected void buildGUI() {

		// Use an absolute layout for this one as well
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		// positioning
		final int componentX = 20;
		final int labelY = -10;

		// instructions
		JLabel cardLabel = new JLabel(
				"""
					<html>Here you can add any textbook PDFs you want to be accessible to
					members of your Discord server. If you added a Syllabus in the
					previous step, CanvaCord can attempt to scan that for textbook titles,
					and may be able to help you find copies online. Otherwise, if you already
					have the files on your computer, you can simply add them here.</html>
					"""
		);
		cardLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		cardLabel.setBounds(componentX, labelY, WIDTH - componentX * 2, 120);
		contentPanel.add(cardLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(componentX, 120, 450, 220);
		contentPanel.add(scrollPane);

		textbooksList = new JList<>();
		scrollPane.getViewport().setView(textbooksList);

		final int buttonX = 494;
		final int buttonY = 134;
		final int buttonWidth = 72;
		final int buttonHeight = 48;
		final int buttonSpacing = 20;

		addButton = new JButton("Add");
		addButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		addButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
		contentPanel.add(addButton);

		scanButton = new JButton("<html>Scan<br/>Syllabus</html>");
		scanButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		scanButton.setBounds(buttonX, buttonY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight);
		contentPanel.add(scanButton);

		// TODO
	}

	@Override
	protected void initLogic() {
		// TODO
	}

	@Override
	protected void prefillGUI(Instance instanceToEdit) {
		// TODO
	}

	public void onNavigateTo() {
		// TODO check for a syllabus from the previous step and enable/disable search button
	}
}
