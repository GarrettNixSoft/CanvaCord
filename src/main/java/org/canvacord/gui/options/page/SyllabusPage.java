package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ImagePanel;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.gui.wizard.cards.instance.CourseAndServerCard;
import org.canvacord.instance.Instance;
import org.canvacord.util.file.CanvaCordPaths;
import org.canvacord.util.file.FileGetter;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class SyllabusPage extends OptionPage {

	private JLabel syllabusFileLabel;
	private JButton findSyllabusButton;
	private JButton addSyllabusButton;
	private JButton clearButton;

	private File syllabusFile;

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

		pdfPreview = new ImagePanel("");
		add(pdfPreview, "cell 0 5 2 1, width 360, height 470");

		// TODO
	}

	@Override
	protected void initLogic() {

		// ================ FINDING SYLLABUS AUTOMATICALLY ================
		findSyllabusButton.addActionListener(event -> {
			String courseID = (String) dataStore.get("courseID");
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
						syllabusFileLabel.setText(file.getAbsolutePath());
						syllabusFile = file;
						previewLabel.setVisible(true);
						renderPreview();
					}
			);
		});

		// ================ CLEARING SELECTION ================
		clearButton.addActionListener(event -> {
			if (UserInput.askToConfirm("Clear selection?", "Confirm Clear")) {
				syllabusFileLabel.setText("None");
				syllabusFile = null;
				previewLabel.setVisible(false);
				pdfPreview.setImage(null);
				revalidate();
				repaint();
			}
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
	}

	@Override
	protected void verifyInputs() throws Exception {
		// TODO
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
