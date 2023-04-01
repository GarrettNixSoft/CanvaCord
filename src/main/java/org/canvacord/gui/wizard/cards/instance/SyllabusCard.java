package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;
import org.canvacord.util.file.FileGetter;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Optional;

public class SyllabusCard extends InstanceConfigCard {

	private JLabel syllabusFileLabel;
	private JButton findSyllabusButton;
	private JButton addSyllabusButton;
	private JButton clearButton;

	private File syllabusFile;

	public SyllabusCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Add Syllabus");
	}

	@Override
	protected void buildGUI() {

		// Use an absolute layout for this one as well
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		int componentX = 40;
		int labelY = -10;
		int rowY = 100;

		int syllabusLabelWidth = 100;
		int spacing = 4;

		int buttonWidth = 80;
		int buttonHeight = 28;

		int buttonY = rowY + 40;

		JLabel cardLabel = new JLabel(
				"""
					<html>You can optionally add your course syllabus to this instance.
					CanvaCord can attempt to find your course syllabus on Canvas
					automatically, or if you already have the file, you can add it
					manually.</html>""");
		cardLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		cardLabel.setBounds(componentX, labelY, WIDTH - componentX * 2, 120);
		contentPanel.add(cardLabel);

		JLabel syllabusLabel = new JLabel("Syllabus File:");
		syllabusLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		syllabusLabel.setBounds(componentX, rowY, syllabusLabelWidth, buttonHeight);
		contentPanel.add(syllabusLabel);

		syllabusFileLabel = new JLabel("None");
		syllabusFileLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		syllabusFileLabel.setBounds(componentX + syllabusLabelWidth + spacing, rowY, WIDTH - componentX * 2 - syllabusLabelWidth - spacing, buttonHeight);
		contentPanel.add(syllabusFileLabel);

		findSyllabusButton = new JButton("Find");
		findSyllabusButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		findSyllabusButton.setBounds(componentX, buttonY, buttonWidth, buttonHeight);
		contentPanel.add(findSyllabusButton);

		addSyllabusButton = new JButton("Add");
		addSyllabusButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		addSyllabusButton.setBounds(componentX + buttonWidth + 10, buttonY, buttonWidth, buttonHeight);
		contentPanel.add(addSyllabusButton);

		clearButton = new JButton("Clear");
		clearButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		clearButton.setBounds(componentX + (buttonWidth + 10) * 2, buttonY, buttonWidth, buttonHeight);
		contentPanel.add(clearButton);

	}

	@Override
	protected void initLogic() {

		// ================ FINDING SYLLABUS AUTOMATICALLY ================
		findSyllabusButton.addActionListener(event -> {
			String courseID = ((CourseAndServerCard) getParentWizard().getCard("course_server")).getCourseID();
			CanvasApi.getInstance().findSyllabus(courseID).ifPresentOrElse(
				syllabusInfo -> {
					syllabusFile = syllabusInfo.getSyllabusFile();
					syllabusFileLabel.setText(syllabusFile.getName());
				},
				() -> {
					UserInput.showWarningMessage("Could not find a Syllabus on Canvas.", "No Results");
				}
			);
		});


		// ================ ADDING SYLLABUS MANUALLY ================
		addSyllabusButton.addActionListener(event -> {
			FileGetter.getFile(System.getProperty("user.dir"), "PDF or DOC files", "pdf", "doc", "docx").ifPresent(
				file -> {
					syllabusFileLabel.setText(file.getPath());
					syllabusFile = file;
				}
			);
		});

		// ================ CLEARING SELECTION ================
		clearButton.addActionListener(event -> {
			if (UserInput.askToConfirm("Clear selection?", "Confirm Clear")) {
				syllabusFileLabel.setText("None");
				syllabusFile = null;
			}
		});

	}

	@Override
	public void prefillGUI(Instance instanceToEdit) {
		// TODO Andrew
		syllabusFile = new File("./instances/" + instanceToEdit.getCourseID() + "_" + instanceToEdit.getServerID() + "/syllabus.pdf");
		syllabusFileLabel.setText(syllabusFile.getPath());
	}

	public Optional<File> getSyllabusFile() {
		return Optional.ofNullable(syllabusFile);
	}
}
