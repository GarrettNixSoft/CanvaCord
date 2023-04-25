package org.canvacord.gui.dialog;

import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.NamedError;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MultiErrorDialog extends CanvaCordDialog {

	private static final int WIDTH = 480;
	private static final int HEIGHT = 720;

	public MultiErrorDialog(String title, String message, List<NamedError> errors) {
		super(title, WIDTH, HEIGHT);
		buildGUI(message, errors);
	}

	@Override
	protected boolean verifyInputs() {
		return true;
	}

	private void buildGUI(String message, List<NamedError> errors) {

		final int componentX = 10;
		final int messageY = 4;

		final int messageWidth = WIDTH - componentX * 3;
		final int messageHeight = 60;

		final int boxY = messageY + messageHeight + 10;
		final int boxWidth = messageWidth;
		final int boxHeight = HEIGHT - boxY * 2 - 10;

		JLabel messageLabel = new JLabel();
		messageLabel.setText(message);
		messageLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		messageLabel.setBounds(componentX, messageY, messageWidth, messageHeight);
		add(messageLabel);

		JList<NamedError> errorList = new JList<>();

		errorList.setFixedCellHeight(-1);
		errorList.setLayoutOrientation(JList.VERTICAL);
		errorList.setCellRenderer(new ErrorCellRenderer(errorList));

		JScrollPane errorPane = new JScrollPane();
		errorPane.setBounds(componentX, boxY, boxWidth, boxHeight);
		errorPane.getViewport().setView(errorList);

		errorList.setModel(buildErrorListModel(errors));

		add(errorPane);

	}

	private AbstractListModel<NamedError> buildErrorListModel(List<NamedError> errors) {

		return new AbstractListModel<>() {
			@Override
			public int getSize() {
				return errors.size();
			}

			@Override
			public NamedError getElementAt(int index) {
				return errors.get(index);
			}
		};



	}

	private static class ErrorCellRenderer extends JLabel implements ListCellRenderer<NamedError> {

		private static final int SIZE = 50;

		public ErrorCellRenderer(JList parent) {
			setPreferredSize(new Dimension(parent.getWidth(), SIZE));
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(SIZE, SIZE);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends NamedError> list, NamedError error, int index, boolean isSelected, boolean cellHasFocus) {

			setText(
					"<html>" +
					"<b>" + error.name() + " > " + error.error().getClass().getSimpleName() + "</b>" +
					"<br/>" +
					error.error().getMessage() +
					"</html>"
			);

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

			setBorder(new EmptyBorder(10, 10, 10, 10));

			return this;
		}
	}

	public static void showMultiErrorDialog(String title, String message, List<NamedError> errors) {
		MultiErrorDialog dialog = new MultiErrorDialog(title, message, errors);
		dialog.setVisible(true);
		dialog.dispose();
	}

}
