package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.exception.FileFormatException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.instance.Instance;
import org.canvacord.util.file.FileGetter;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

public class NameIconPage extends OptionPage {

	private JTextField nameField;
	private JTextField iconPathField;
	private JButton chooseButton;

	public NameIconPage() {
		super("Name and Icon");
	}

	@Override
	protected void buildGUI() {

		// prepare the layout
		setLayout(new MigLayout("insets 10 10 10 10", "[][][]", "[][]"));

		// instance name editing
		JLabel label = new JLabel("Instance Name:");
		label.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		add(label, "cell 0 0");

		nameField = new JTextField(32);
		nameField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(nameField, "cell 1 0");

		// icon path editing
		JLabel iconPathLabel = new JLabel("Icon Path:");
		iconPathLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		add(iconPathLabel, "cell 0 2");

		iconPathField = new JTextField(48);
		iconPathField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(iconPathField, "cell 1 2");

		chooseButton = new JButton("Choose");
		chooseButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(chooseButton, "cell 2 2");

	}

	@Override
	protected void initLogic() {
		chooseButton.addActionListener(event -> {
			FileGetter.getFile(System.getProperty("user.dir"), "Image files", "png", "jpg", "jpeg").ifPresent(
					file -> iconPathField.setText(file.getPath())
			);
		});
	}

	@Override
	protected void prefillGUI() {
		nameField.setText((String) dataStore.get("name"));
		iconPathField.setText((String) dataStore.get("icon_path"));
	}

	@Override
	protected void verifyInputs() throws Exception {

		// TODO validate name

		// Validate icon path
		String iconPath = iconPathField.getText();

		if (!(iconPath.isBlank() || iconPath.equals("default"))) {
			File iconFile = new File(iconPath);
			if (!iconFile.exists())
				throw new FileNotFoundException("The file pointed to by the provided Icon Path does not exist.");
			if (!FileUtil.isValidFile(iconPath, "png", "jpg", "jpeg"))
				throw new FileFormatException("Icon path must be an existing file in PNG or JPG format.");
		}

	}
}
