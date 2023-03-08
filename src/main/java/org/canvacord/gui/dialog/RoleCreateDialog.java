package org.canvacord.gui.dialog;

import org.canvacord.discord.CanvaCordRole;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

public class RoleCreateDialog extends JDialog {

	private static final int WIDTH = 360;
	private static final int HEIGHT = 240;

	private JTextField roleNameField;
	private ColorPanel roleColorPanel;

	private JButton okButton;
	private JButton cancelButton;

	private boolean cancelled;

	public RoleCreateDialog() {
		super();

		// basic config
		setTitle("New Role");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setResizable(false);

		// capture clicking the X
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// center in display
		setLocationRelativeTo(null);

		buildGUI();
		initLogic();
	}

	public RoleCreateDialog(CanvaCordRole roleToEdit) {
		this();
		prefillGUI(roleToEdit);
	}

	private void buildGUI() {

		// Use absolute positioning
		setLayout(null);

		// Use an internal panel for components


		// Positioning constants
		final int componentX = 20;
		final int colorPanelSize = 50;

		final int nameLabelY = 4;
		final int nameFieldY = nameLabelY + 30;
		final int colorLabelY = nameFieldY + 30;
		final int colorPanelY = colorLabelY + 32;

		final int buttonWidth = 80;
		final int buttonHeight = 28;
		final int buttonSpacing = 10;
		final int buttonY = colorPanelY + 70;

		// Label the name field
		JLabel nameFieldLabel = new JLabel("Choose a name for the Role:");
		nameFieldLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		nameFieldLabel.setBounds(componentX, nameLabelY, 240, 24);
		add(nameFieldLabel);

		// Build the name field
		roleNameField = new JTextField(24);
		roleNameField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		roleNameField.setBounds(componentX, nameFieldY, 300, 24);
		add(roleNameField);

		// Label the color chooser
		JLabel colorChooserLabel = new JLabel("Choose a color for the role:");
		colorChooserLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		colorChooserLabel.setBounds(componentX, colorLabelY, 240, 24);
		add(colorChooserLabel);

		// Add the color panel
		roleColorPanel = new ColorPanel(Color.RED);
		roleColorPanel.setBounds(WIDTH / 2 - colorPanelSize / 2, colorPanelY, colorPanelSize, colorPanelSize);
		add(roleColorPanel);

		roleColorPanel.setDoBorder(true);

		// Add dialog buttons
		okButton = new JButton("OK");
		okButton.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		okButton.setBounds(WIDTH / 2 - buttonWidth - buttonSpacing / 2, buttonY, buttonWidth, buttonHeight);
		add(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		cancelButton.setBounds(WIDTH / 2 + buttonSpacing / 2, buttonY, buttonWidth, buttonHeight);
		add(cancelButton);

	}

	private void initLogic() {

		final Component parent = this;

		roleColorPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(parent, "Select Role Color", roleColorPanel.getColor());
				roleColorPanel.setColor(newColor);
				parent.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// nothing
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// nothing
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// nothing
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// nothing
			}
		});

		okButton.addActionListener(event -> {
			cancelled = false;
			setVisible(false);
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

	private void prefillGUI(CanvaCordRole roleToEdit) {
		roleNameField.setText(roleToEdit.getName());
		roleColorPanel.setColor(roleToEdit.getColor());
	}

	private Optional<CanvaCordRole> getResult() {
		if (cancelled)
			return Optional.empty();
		else {
			return Optional.of(new CanvaCordRole(roleColorPanel.getColor(), roleNameField.getText()));
		}
	}

	public static Optional<CanvaCordRole> buildRole() {

		RoleCreateDialog dialog = new RoleCreateDialog();
		dialog.setVisible(true);
		dialog.dispose();

		return dialog.getResult();
	}

	public static Optional<CanvaCordRole> editRole(CanvaCordRole roleToEdit) {

		RoleCreateDialog dialog = new RoleCreateDialog(roleToEdit);
		dialog.setVisible(true);
		dialog.dispose();

		return dialog.getResult();
	}

}
