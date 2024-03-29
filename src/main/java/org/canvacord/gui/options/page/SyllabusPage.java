package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ImagePanel;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.instance.Instance;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileGetter;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SyllabusPage extends OptionPage {

	private JLabel syllabusFileLabel;
	private JButton findSyllabusButton;
	private JButton addSyllabusButton;
	private JButton clearButton;

	private File syllabusFile;
	private boolean syllabusChanged;

	private JLabel previewLabel;
	private ImagePanel pdfPreview;

	public SyllabusPage() {
		super("Syllabus");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("insets 10 10 10 10", "[][grow][]", "[][][][][][grow]"));

		JLabel syllabusLabel = new JLabel("Syllabus:");
		syllabusLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		add(syllabusLabel, "cell 0 0");

		syllabusFileLabel = new JLabel("");
		syllabusFileLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		add(syllabusFileLabel, "span");

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout("", "[][][]", "[]"));
		add(buttonPanel, "cell 1 2");

		findSyllabusButton = new JButton("Find");
		findSyllabusButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		buttonPanel.add(findSyllabusButton, "cell 0 0");

		addSyllabusButton = new JButton("Add");
		addSyllabusButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		buttonPanel.add(addSyllabusButton, "cell 1 0");

		clearButton = new JButton("Clear");
		clearButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		buttonPanel.add(clearButton, "cell 2 0");

		previewLabel = new JLabel("Preview:");
		previewLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		previewLabel.setVisible(false);
		add(previewLabel, "cell 0 4");

		pdfPreview = ImagePanel.loadFromDisk("");
		add(pdfPreview, "cell 0 5 2 1, width 360, height 470");

		// TODO
	}

	@Override
	protected void initLogic() {

		// ================ FINDING SYLLABUS AUTOMATICALLY ================
		findSyllabusButton.addActionListener(event -> {
			String courseID = (String) dataStore.get("course_id");
			CanvasApi.getInstance().findSyllabus(courseID).ifPresentOrElse(
					syllabusInfo -> {
						syllabusFile = syllabusInfo.getSyllabusFile();
						syllabusFileLabel.setText(syllabusFile.getName());
						syllabusChanged = true;
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
						syllabusFileLabel.setText(file.getAbsolutePath());
						syllabusFile = file;
						previewLabel.setVisible(true);
						renderPreview();
						syllabusChanged = true;
					}
			);
		});

		// ================ CLEARING SELECTION ================
		clearButton.addActionListener(event -> {
			if (UserInput.askToConfirm("Clear selection?\n\n(This will delete the local\ncopy of the PDF file.)", "Confirm Clear")) {
				syllabusFileLabel.setText("None");
				syllabusFile = null;
				previewLabel.setVisible(false);
				pdfPreview.setImage(null);
				revalidate();
				repaint();
				syllabusChanged = true;
			}
		});

		// ================ SYLLABUS FLAG ================
		setOnNavigateAway(() -> {
			dataStore.store("has_syllabus", syllabusFile != null);
			dataStore.store("syllabus_path", syllabusFileLabel.getText());
		});

	}

	@Override
	protected void prefillGUI() {
		if ((Boolean) dataStore.get("has_syllabus")) {
			Instance instance = (Instance) dataStore.get("instance");
			syllabusFile = CanvaCordPaths.getInstanceSyllabusPath(instance).toFile();
			syllabusFileLabel.setText("syllabus.pdf");
			renderPreview();
		}
		else {
			syllabusFileLabel.setText("None");
		}
	}

	@Override
	protected void verifyInputs() throws Exception {

		// don't do anything for no change
		if (!syllabusChanged) return;

		dataStore.store("has_syllabus", syllabusFile != null);
		Path syllabusPath = CanvaCordPaths.getInstanceSyllabusPath((Instance) dataStore.get("instance"));

		if (syllabusFile == null) {
			Files.deleteIfExists(syllabusPath);
		}
		else if (syllabusChanged) {
			// Move the new file into the directory as the new syllabus file
			Files.copy(syllabusFile.toPath(), syllabusPath, StandardCopyOption.REPLACE_EXISTING);
			syllabusChanged = false;
			System.out.println("Copied syllabus.pdf");
			// If an error occurs in the copy, it will be thrown up to the OptionsPanel and added to the error list
		}

	}

	private void renderPreview() {
		try (PDDocument document = PDDocument.load(syllabusFile)) {

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			BufferedImage preview = pdfRenderer.renderImage(0);
			pdfPreview.setImage(preview);

			previewLabel.setVisible(true);

			revalidate();
			repaint();

		}
		catch (IOException e) {
			// TODO show file failed to load message
			UserInput.showExceptionWarning(e);
			e.printStackTrace();
		}
	}

}
