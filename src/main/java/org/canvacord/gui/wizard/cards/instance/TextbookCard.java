package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.canvas.TextbookInfo;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorIcon;
import org.canvacord.gui.dialog.TextbookManualDialog;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;
import org.canvacord.util.file.FileHasher;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import java.awt.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TextbookCard extends InstanceConfigCard {

	private List<TextbookInfo> textbooks;
	private JList<TextbookInfo> textbooksList;

	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;
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
		final int buttonY = 120;
		final int buttonWidth = 72;
		final int buttonHeight = 48;
		final int buttonSpacing = 8;

		addButton = new JButton("Add");
		addButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		addButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
		contentPanel.add(addButton);

		editButton = new JButton("Edit");
		editButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		editButton.setBounds(buttonX, buttonY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight);
		contentPanel.add(editButton);

		deleteButton = new JButton("Delete");
		deleteButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		deleteButton.setBounds(buttonX, buttonY + (buttonHeight + buttonSpacing) * 2, buttonWidth, buttonHeight);
		contentPanel.add(deleteButton);

		scanButton = new JButton("<html>Scan<br/>Syllabus</html>");
		scanButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		scanButton.setBounds(buttonX, buttonY + (buttonHeight + buttonSpacing) * 3, buttonWidth, buttonHeight);
		contentPanel.add(scanButton);

		// SHOWING TESTBOOKS IN THE LIST
		textbooksList.setFixedCellHeight(-1);
		textbooksList.setLayoutOrientation(JList.VERTICAL);
		textbooksList.setCellRenderer(new TextbookCard.TextbookCellRenderer(textbooksList));

		// TODO
	}

	@Override
	protected void initLogic() {

		// init collections
		textbooks = new ArrayList<>();

		// ================ ADDING TEXTBOOKS MANUALLY ================
		addButton.addActionListener(event -> {

			TextbookManualDialog.addNewTextbook().ifPresent(
					textbookInfo -> {
						textbooks.add(textbookInfo);
						updateTextbooksList();
					}
			);

		});

		// ================ EDITING TEXTBOOKS ================
		editButton.addActionListener(event -> {
			TextbookInfo infoToEdit = textbooksList.getSelectedValue();
			if (infoToEdit == null) return;
			int index = textbooksList.getSelectedIndex();
			TextbookManualDialog.editTextbook(infoToEdit).ifPresent(
					editedInfo -> {
						textbooks.set(index, editedInfo);
						updateTextbooksList();
					}
			);
		});

		// ================ DELETING TEXTBOOKS ================
		deleteButton.addActionListener(event -> {
			int index = textbooksList.getSelectedIndex();
			if (index == -1) return;
			if (UserInput.askToConfirm("Delete this textbook entry?\n(No original files will be affected.)", "Confirm Delete")) {
				textbooks.remove(index);
				updateTextbooksList();
			}
		});

		// ================ SCANNING THE SYLLABUS ================
		scanButton.addActionListener(event -> {

			// TODO
			UserInput.showWarningMessage("Not implemented yet. :/", "To Be Continued");

		});

	}

	@Override
	public void prefillGUI(Instance instanceToEdit) {
		// TODO
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

		textbooksList.setModel(textbooksListModel);
		textbooksList.updateUI();

	}

	public void onNavigateTo() {
		// TODO check for a syllabus from the previous step and enable/disable search button

		if (((SyllabusCard) getParentWizard().getCard("syllabus_config")).getSyllabusFile().isEmpty()) {
			scanButton.setEnabled(false);
			scanButton.setToolTipText("<html>You need to add a Syllabus file<br/>in order to scan for textbooks.</html>");
		}
		else {
			scanButton.setEnabled(true);
			scanButton.setToolTipText(null);
		}

	}

	private static class TextbookCellRenderer extends JLabel implements ListCellRenderer<TextbookInfo> {

		private static final int SIZE = 50;

		public TextbookCellRenderer(JList parent) {
			setPreferredSize(new Dimension(parent.getWidth(), SIZE));
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(SIZE, SIZE);
		}

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

	public List<TextbookInfo> getTextbooks() {
		return textbooks;
	}
}
