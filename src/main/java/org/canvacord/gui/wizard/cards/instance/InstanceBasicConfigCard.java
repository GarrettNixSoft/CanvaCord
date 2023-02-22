package org.canvacord.gui.wizard.cards.instance;

import net.miginfocom.swing.MigLayout;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ImagePanel;
import org.canvacord.gui.component.TextPrompt;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
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

		// ================ GUI SUB-PANEL ================
		cardPanel.setLayout(new BorderLayout());

		// ================ HEADER ================
		ImagePanel topBar = new ImagePanel("resources/setup_topbar.png");
		topBar.setLayout(null);
//		topBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		topBar.setPreferredSize(new Dimension(getMaximumSize().width, 80));
		cardPanel.add(topBar, BorderLayout.NORTH);

		JLabel cardHeader = new JLabel("Basic Settings");
		cardHeader.setFont(CanvaCordFonts.HEADER_FONT);
		cardHeader.setBounds(30, 25, 300, 30);
		topBar.add(cardHeader);

		// ================ MAIN CONTENT ================
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		int componentX = 60;
		int fieldWidth = 400;

		JLabel nameLabel = new JLabel("Enter a name for your Instance:");
		nameLabel.setFont(CanvaCordFonts.LABEL_FONT_LARGE);
		nameLabel.setBounds(componentX, 75, 300, 24);
		contentPanel.add(nameLabel);

		nameField = new JTextField(50);
		nameField.setFont(CanvaCordFonts.LABEL_FONT);
		nameField.setBounds(componentX, 106, fieldWidth, 24);
		contentPanel.add(nameField);

		TextPrompt namePrompt = new TextPrompt("My Instance", nameField);
		namePrompt.setFont(CanvaCordFonts.LABEL_FONT);
		namePrompt.setForeground(Color.GRAY);

		JLabel pathLabel = new JLabel("Choose an icon file (Optional):");
		pathLabel.setFont(CanvaCordFonts.LABEL_FONT_LARGE);
		pathLabel.setBounds(componentX, 180, 300, 24);
		contentPanel.add(pathLabel);

		iconPathField = new JTextField(260);
		iconPathField.setFont(CanvaCordFonts.LABEL_FONT);
		iconPathField.setBounds(componentX, 211, fieldWidth, 24);
		contentPanel.add(iconPathField);

		TextPrompt pathPrompt = new TextPrompt("Path to file...", iconPathField);
		pathPrompt.setFont(CanvaCordFonts.LABEL_FONT);
		pathPrompt.setForeground(Color.GRAY);

		chooseFileButton = new JButton("Choose...");
		chooseFileButton.setFont(CanvaCordFonts.LABEL_FONT);
		chooseFileButton.setBounds(componentX + fieldWidth + 20, 211, 90, 24);
		contentPanel.add(chooseFileButton);

	}

	@Override
	protected void initLogic() {

		chooseFileButton.addActionListener(event -> {

			FileGetter.getFile(System.getProperty("user.dir"), "Image files", "png", "jpg", "jpeg")
					.ifPresent(file -> iconPathField.setText(file.getAbsolutePath()));

		});

	}

	public String getInstanceName() {
		return nameField.getText();
	}

	public String getIconPath() {
		return iconPathField.getText();
	}

}
