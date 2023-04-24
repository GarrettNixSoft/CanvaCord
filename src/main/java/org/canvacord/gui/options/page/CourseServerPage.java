package org.canvacord.gui.options.page;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionsPanel;
import org.canvacord.instance.Instance;

import javax.swing.*;
import java.awt.*;

public class CourseServerPage extends InstanceOptionsPage {

	private JTextField courseIDField;
	private JTextField serverIDField;

	public CourseServerPage(Instance instanceToEdit) {
		super("Course and Server", instanceToEdit);
	}

	@Override
	protected void buildGUI() {

		Dimension labelSize = new Dimension(300, 28);

		Dimension minFieldSize = new Dimension(240, 28);
		Dimension fieldSize = new Dimension(300, 28);
		Dimension maxFieldSize = new Dimension(360, 28);

		Dimension minPanelSize = new Dimension(OptionsPanel.MIN_WIDTH - OptionsPanel.MIN_LIST_WIDTH, 60);
		Dimension panelSize = new Dimension(400, 60);
		Dimension maxPanelSize = new Dimension(OptionsPanel.MAX_WIDTH - OptionsPanel.MAX_LIST_WIDTH, 60);

		setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		add(mainPanel, BorderLayout.CENTER);

		JPanel coursePanel = new JPanel();
		coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.X_AXIS));
		coursePanel.setMinimumSize(minPanelSize);
		coursePanel.setPreferredSize(panelSize);
		coursePanel.setMinimumSize(maxPanelSize);
		mainPanel.add(coursePanel);

		mainPanel.add(Box.createVerticalStrut(30));

		JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.X_AXIS));
		serverPanel.setMinimumSize(minPanelSize);
		serverPanel.setPreferredSize(panelSize);
		serverPanel.setMinimumSize(maxPanelSize);
		mainPanel.add(serverPanel);

		JLabel courseLabel = new JLabel("Course ID:");
		courseLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		courseLabel.setPreferredSize(labelSize);
		coursePanel.add(courseLabel);

		coursePanel.add(Box.createHorizontalStrut(20));

		courseIDField = new JTextField(24);
		courseIDField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		courseIDField.setMinimumSize(minFieldSize);
		courseIDField.setPreferredSize(fieldSize);
		courseIDField.setMaximumSize(maxFieldSize);
		courseIDField.setEditable(false);
		coursePanel.add(courseIDField);

		JLabel serverLabel = new JLabel("Server ID:");
		serverLabel.setAlignmentX(LEFT_ALIGNMENT);
		serverLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		serverLabel.setPreferredSize(labelSize);
		serverPanel.add(serverLabel);

		serverPanel.add(Box.createHorizontalStrut(20));

		serverIDField = new JTextField(24);
		serverIDField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		serverIDField.setMinimumSize(minFieldSize);
		serverIDField.setPreferredSize(fieldSize);
		serverIDField.setMaximumSize(maxFieldSize);
		serverIDField.setEditable(false);
		serverPanel.add(serverIDField);

	}

	@Override
	protected void initLogic() {
		// TODO
	}

	@Override
	protected void prefillGUI() {
		courseIDField.setText((String) dataStore.get("course_id"));
		serverIDField.setText(dataStore.get("server_id").toString());
	}

	@Override
	protected void verifyInputs() throws CanvaCordException {
		// nothing, because these inputs cannot be changed
		// they are present only for completeness and visibility
	}
}
