package org.canvacord.gui.dialog;

import org.canvacord.gui.CanvaCordFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class CanvaCordDialog extends JDialog {

	// control button constants
	protected static final int BUTTON_WIDTH = 80;
	protected static final int BUTTON_HEIGHT = 28;
	protected static final int BUTTON_SPACING = 10;

	// size fields
	private final int width, height;

	// control buttons
	private JButton okButton;
	private JButton cancelButton;

	// detecting cancellation
	protected boolean cancelled;

	public CanvaCordDialog(String title, int width, int height) {
		super();

		// save dimensions
		this.width = width;
		this.height = height;

		// basic config
		setTitle(title);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setMaximumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setResizable(false);

		// capture clicking the X
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Center in display
		setLocationRelativeTo(null);

		// Dialogs should use absolute positioning
		setLayout(null);

		// build common components
		buildOkButton();
		buildCancelButton();
		initControlButtonLogic();

	}

	private void buildOkButton() {
		okButton = new JButton("OK");
		okButton.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		okButton.setBounds(width / 2 - BUTTON_WIDTH - BUTTON_SPACING / 2, height - BUTTON_HEIGHT * 2 - BUTTON_SPACING * 2, BUTTON_WIDTH, BUTTON_HEIGHT);
		add(okButton);
	}

	private void buildCancelButton() {
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		cancelButton.setBounds(width / 2 + BUTTON_SPACING / 2, height - BUTTON_HEIGHT * 2 - BUTTON_SPACING * 2, BUTTON_WIDTH, BUTTON_HEIGHT);
		add(cancelButton);
	}

	private void initControlButtonLogic() {

		okButton.addActionListener(event -> {
			if (verifyInputs()) {
				cancelled = false;
				setVisible(false);
			}
		});

		cancelButton.addActionListener(event -> {
			cancelled = true;
			setVisible(false);
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});
	}

	protected abstract boolean verifyInputs();

}
