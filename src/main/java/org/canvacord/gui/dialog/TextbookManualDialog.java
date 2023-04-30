package org.canvacord.gui.dialog;

import org.canvacord.canvas.TextbookInfo;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.TextPrompt;
import org.canvacord.util.file.FileGetter;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class TextbookManualDialog extends CanvaCordDialog {

	private static final int WIDTH = 360;
	private static final int HEIGHT = 280;

	private JTextField titleField;

	private JTextField authorField;

	private JTextField pathField;
	private JButton chooseButton;

	public TextbookManualDialog() {
		super("Add a Textbook", WIDTH, HEIGHT);
		buildGUI();
		initLogic();
	}

	public TextbookManualDialog(TextbookInfo infoToEdit) {
		this();
		prefillGUI(infoToEdit);
	}

	@Override
	protected boolean verifyInputs() {

		// Title field cannot be empty
		if (titleField.getText().isBlank()) {
			UserInput.showErrorMessage("Textbook title cannot be blank.", "Blank Title");
			return false;
		}

		// Author field cannot be empty
		if (authorField.getText().isBlank()) {
			UserInput.showErrorMessage("Please provide an author name.", "No Authors");
			return false;
		}

		// Path field cannot be empty
		if (pathField.getText().isBlank()) {
			UserInput.showErrorMessage("Please choose a textbook file.", "No File Selected");
			return false;
		}

		// Path field must contain a path to a valid PDF file
		if (!FileUtil.isValidFile(pathField.getText(), "pdf")) {
			UserInput.showErrorMessage("Please choose a valid textbook file.", "Invalid Selection");
			return false;
		}

		return true;
	}

	private void buildGUI() {

		// ================ POSITIONING ================
		final int componentX = 20;
		final int titleLabelY = 4;
		final int titleFieldY = titleLabelY + 30;
		final int authorLabelY = titleFieldY + 36;
		final int authorFieldY = authorLabelY + 30;
		final int pathLabelY = authorFieldY + 36;
		final int pathFieldY = pathLabelY + 30;

		final int pathFieldWidth = 210;
		final int spacing = 10;

		JLabel titleLabel = new JLabel("Title:");
		titleLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		titleLabel.setBounds(componentX, titleLabelY, 100, 24);
		add(titleLabel);

		titleField = new JTextField(24);
		titleField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		titleField.setBounds(componentX, titleFieldY, WIDTH - componentX * 3, 28);
		add(titleField);

		TextPrompt titlePrompt = new TextPrompt("Subject, Random Subtitle, 4th ed.", titleField);
		titlePrompt.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		titlePrompt.setForeground(Color.GRAY);

		JLabel authorLabel = new JLabel("Author(s):");
		authorLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		authorLabel.setBounds(componentX, authorLabelY, 100, 24);
		add(authorLabel);

		authorField = new JTextField(24);
		authorField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		authorField.setBounds(componentX, authorFieldY, WIDTH - componentX * 3, 28);
		add(authorField);

		TextPrompt authorPrompt = new TextPrompt("Bob Smith, Alice Doe", authorField);
		authorPrompt.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		authorPrompt.setForeground(Color.GRAY);

		JLabel pathLabel = new JLabel("File Path:");
		pathLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		pathLabel.setBounds(componentX, pathLabelY, 80, 24);
		add(pathLabel);

		pathField = new JTextField(24);
		pathField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		pathField.setBounds(componentX, pathFieldY, pathFieldWidth, 28);
		add(pathField);

		chooseButton = new JButton("Choose");
		chooseButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		chooseButton.setBounds(componentX + pathFieldWidth + spacing, pathFieldY, 80, 28);
		add(chooseButton);

	}

	private void initLogic() {

		// ================ CHOOSE A FILE ================
		chooseButton.addActionListener(event -> {
			FileGetter.getFileRecent("PDF files", "pdf").ifPresent(
					file -> {
						pathField.setText(file.getAbsolutePath());
					}
			);
		});

	}

	private void prefillGUI(TextbookInfo infoToEdit) {
		titleField.setText(infoToEdit.getTitle());
		authorField.setText(infoToEdit.getAuthor());
		pathField.setText(Objects.requireNonNull(infoToEdit.getTextbookFile()).getAbsolutePath());
	}

	public Optional<TextbookInfo> getResult() {
		if (cancelled || !verifyInputs())
			return Optional.empty();
		else {
			JSONObject textbookJSON = new JSONObject();
			textbookJSON.put("title", titleField.getText());
			textbookJSON.put("author", authorField.getText());
			textbookJSON.put("file_path", pathField.getText());
			return Optional.of(new TextbookInfo(textbookJSON));
		}
	}

	public static Optional<TextbookInfo> addNewTextbook() {
		TextbookManualDialog dialog = new TextbookManualDialog();
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

	public static Optional<TextbookInfo> editTextbook(TextbookInfo infoToEdit) {
		TextbookManualDialog dialog = new TextbookManualDialog(infoToEdit);
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

}
