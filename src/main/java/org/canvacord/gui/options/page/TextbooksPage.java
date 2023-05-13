package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.canvas.TextbookFetcher;
import org.canvacord.canvas.TextbookInfo;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorIcon;
import org.canvacord.gui.dialog.TextbookManualDialog;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.util.file.FileHasher;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.resources.ImageLoader;
import org.canvacord.util.time.LongTaskDialog;
import org.json.JSONArray;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class TextbooksPage extends OptionPage {

	private List<TextbookInfo> textbooks;
	private JList<TextbookInfo> textbookList;

	private List<TextbookInfo> newTextbooks;
	private List<TextbookInfo> removedTextbooks;

	private JButton newTextbookButton;
	private JButton editTextbookButton;
	private JButton deleteTextbookButton;
	private JButton scanButton;
	private JButton searchButton;

	public TextbooksPage() {
		super("Textbooks");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("insets 10 10 10 10",
				"[grow][]", "[]".repeat(20)));

		JLabel textbooksLabel = new JLabel("Textbooks:");
		textbooksLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(textbooksLabel, "cell 0 0");

		textbookList = new JList<>();
		textbookList.setFixedCellHeight(-1);
		textbookList.setLayoutOrientation(JList.VERTICAL);
		textbookList.setCellRenderer(new TextbookCellRenderer());

		JScrollPane textbookPane = new JScrollPane();
		textbookPane.getViewport().setView(textbookList);
		add(textbookPane, "cell 0 1 5 18, growx, growy");

		newTextbookButton = new JButton(ImageLoader.loadIcon("new_icon.png"));
		add(newTextbookButton, "cell 9 3, growx");

		editTextbookButton = new JButton(ImageLoader.loadIcon("edit_icon_wip.png"));
		add(editTextbookButton, "cell 9 6, growx");

		deleteTextbookButton = new JButton(ImageLoader.loadIcon("delete_icon_non_beveled.png"));
		add(deleteTextbookButton, "cell 9 9, growx");

		scanButton = new JButton("<html>Scan<br/>Syllabus</html>");
		scanButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(scanButton, "cell 9 12, growx");

		searchButton = new JButton("<html>Search<br/>Online</html>");
		searchButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(searchButton, "cell 9 15, growx");

		// TODO
	}

	@Override
	protected void initLogic() {

		// ================ ADDING TEXTBOOKS MANUALLY ================
		newTextbookButton.addActionListener(event -> {

			TextbookManualDialog.addNewTextbook().ifPresent(
					textbookInfo -> {
						textbooks.add(textbookInfo);
						newTextbooks.add(textbookInfo);
						updateTextbooksList();
					}
			);

		});

		// ================ EDITING TEXTBOOKS ================
		editTextbookButton.addActionListener(event -> {
			TextbookInfo infoToEdit = textbookList.getSelectedValue();
			if (infoToEdit == null) return;
			int index = textbookList.getSelectedIndex();
			TextbookManualDialog.editTextbook(infoToEdit).ifPresent(
					editedInfo -> {
						textbooks.set(index, editedInfo);
						updateTextbooksList();
					}
			);
		});

		// ================ DELETING TEXTBOOKS ================
		deleteTextbookButton.addActionListener(event -> {
			int index = textbookList.getSelectedIndex();
			if (index == -1) return;
			if (UserInput.askToConfirm("Delete this textbook entry?\n(No original files will be affected.)", "Confirm Delete")) {
				removedTextbooks.add(textbooks.remove(index));
				updateTextbooksList();
			}
		});

		// ================ SCANNING THE SYLLABUS ================
		scanButton.addActionListener(event -> {

			// TODO
			UserInput.showWarningMessage("Not implemented yet. :/", "To Be Continued");

		});

		setOnNavigateTo(() -> {
			boolean hasSyllabus = (Boolean) dataStore.get("has_syllabus");
			if (hasSyllabus) {
				scanButton.setEnabled(true);
				scanButton.setToolTipText(null);
			}
			else {
				scanButton.setEnabled(false);
				scanButton.setToolTipText("<html>You must add a Syllabus file in<br/>order to scan for textbooks.</html>");
			}
		});

		// ================ SEARCHING FOR TEXTBOOKS ================
		searchButton.addActionListener(event -> {
			String searchTerm = UserInput.getUserString("Enter a title to search:");
			if (searchTerm != null && !searchTerm.isBlank()) {

				AtomicReference<Optional<TextbookInfo>> result = new AtomicReference<>();

				LongTaskDialog.runLongTask(() -> {
					result.set(TextbookFetcher.fetchTextbookOnline(searchTerm, (String) dataStore.get("course_id")));
				}, "Searching online for " + searchTerm, "Searching");


				result.get().ifPresentOrElse(
						textbookInfo -> {
							// TODO more here
							UserInput.showMessage("Downloaded: " + textbookInfo.getTitle(), "Success");
							textbooks.add(textbookInfo);
							updateTextbooksList();
						},
				() -> {
							UserInput.showMessage("No results were found.", "No Results");
						}
				);

			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void prefillGUI() {
		textbooks = (List<TextbookInfo>) dataStore.get("textbooks");
		updateTextbooksList();

		newTextbooks = new ArrayList<>();
		removedTextbooks = new ArrayList<>();
	}

	@Override
	protected void verifyInputs() throws Exception {

		// Clear files for removed textbooks
		for (TextbookInfo removedTextbook : removedTextbooks) {
			Files.deleteIfExists(removedTextbook.getTextbookFile().toPath());
		}
		removedTextbooks.clear();

		// Copy new textbooks into the instance directory
		for (TextbookInfo newTextbook : newTextbooks) {
			newTextbook.storeAndConvert();
		}
		newTextbooks.clear();

		dataStore.store("textbooks", getTextbooksArray());

	}

	private void updateTextbooksList() {

		ListModel<TextbookInfo> textbooksListModel = new AbstractListModel<>() {
			@Override
			public int getSize() {
				return textbooks.size();
			}

			@Override
			public TextbookInfo getElementAt(int index) {
				return textbooks.get(index);
			}
		};

		textbookList.setModel(textbooksListModel);
		textbookList.updateUI();

	}

	private static class TextbookCellRenderer extends JLabel implements ListCellRenderer<TextbookInfo> {

		private static final int SIZE = 50;

		@Override
		public Component getListCellRendererComponent(JList<? extends TextbookInfo> list, TextbookInfo textbook, int index, boolean isSelected, boolean cellHasFocus) {

			File textbookFile = textbook.getTextbookFile();

			String sizeStr;
			if (textbookFile != null)
				sizeStr = FileUtil.getSizeString(textbookFile);
			else
				sizeStr = "0B";

			setText("<html><b>" + textbook.getTitle() + "</b><br/>" + textbook.getAuthor() + "<br/>" + sizeStr + "</html>");

			ColorIcon colorIcon = new ColorIcon(getTextbookColor(textbook), SIZE, SIZE);
			colorIcon.setDoBorder(true);
			setIcon(colorIcon);

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			setOpaque(true);

			return this;
		}

		private Color getTextbookColor(TextbookInfo textbook) {

			// missing files get the pitch black void of nothingness
			if (textbook.getTextbookFile() == null)
				return Color.BLACK;

			/*
				This is an absolutely ridiculous way of selecting a color for a textbook,
				but it does guarantee that any given file will always get the exact same color.
			 */

			// Hash the file
			String hash = FileHasher.hashFile(textbook.getTextbookFile());

			// Build a seed out of the characters in the hash
			long seed = 0;
			for (char c : hash.toCharArray()) {
				seed += (int) c;
			}

			// Seed a PRNG and (deterministically) generate a random color with it
			Random random = new Random(seed);
			return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));

		}

	}

	private JSONArray getTextbooksArray() {
		JSONArray textbookFiles = new JSONArray();
		for (TextbookInfo bookInfo : textbooks) {
			textbookFiles.put(bookInfo.textbookJSON());
		}
		return textbookFiles;
	}

}
