package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.TextPrompt;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;
import org.canvacord.util.file.FileGetter;

import javax.swing.*;
import java.awt.*;

public class InstanceBasicConfigCard extends InstanceConfigCard {

	private JTextField nameField;
	private JTextField iconPathField;
	private JButton chooseFileButton;

	public InstanceBasicConfigCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Basic Settings");
	}

	@Override
	protected void buildGUI() {

		// ================ MAIN CONTENT ================
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		// Alignment of components
		int componentX = 60;
		int fieldWidth = 400;

		// Use a label to instruct the user
		JLabel nameLabel = new JLabel("Enter a name for your Instance:");
		nameLabel.setFont(CanvaCordFonts.LABEL_FONT_LARGE);
		nameLabel.setBounds(componentX, 75, 300, 24);
		contentPanel.add(nameLabel);

		// Use a text field to accept a string
		nameField = new JTextField(50);
		nameField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		nameField.setBounds(componentX, 106, fieldWidth, 24);
		contentPanel.add(nameField);

		// Use a prompt to give an example input
		TextPrompt namePrompt = new TextPrompt("My Instance", nameField);
		namePrompt.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		namePrompt.setForeground(Color.GRAY);

		// Use a label to instruct the user to enter a file path if they wish to
		JLabel pathLabel = new JLabel("Choose an icon file (Optional):");
		pathLabel.setFont(CanvaCordFonts.LABEL_FONT_LARGE);
		pathLabel.setBounds(componentX, 180, 300, 24);
		contentPanel.add(pathLabel);

		// Use a text field to store input
		iconPathField = new JTextField(260);
		iconPathField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		iconPathField.setBounds(componentX, 211, fieldWidth, 24);
		contentPanel.add(iconPathField);

		// Use a prompt to give a hint
		TextPrompt pathPrompt = new TextPrompt("Path to file...", iconPathField);
		pathPrompt.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		pathPrompt.setForeground(Color.GRAY);

		// Allow the user to click a button to choose a file more easily
		chooseFileButton = new JButton("Choose...");
		chooseFileButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		chooseFileButton.setBounds(componentX + fieldWidth + 20, 211, 90, 24);
		contentPanel.add(chooseFileButton);

	}

	@Override
	protected void initLogic() {

		// Program the button to run a file chooser looking for PNG or JPG images
		chooseFileButton.addActionListener(event -> {

			FileGetter.getFile(System.getProperty("user.dir"), "Image files", "png", "jpg", "jpeg")
					.ifPresent(file -> iconPathField.setText(file.getAbsolutePath()));

		});

	}

	@Override
	public void prefillGUI(Instance instanceToEdit) {
		// TODO Andrew
	}

	public String getInstanceName() {
		return nameField.getText();
	}

	public String getIconPath() {
		return iconPathField.getText();
	}

}
