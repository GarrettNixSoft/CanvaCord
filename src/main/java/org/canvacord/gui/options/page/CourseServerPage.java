package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.gui.options.OptionsPanel;
import org.canvacord.instance.Instance;

import javax.swing.*;
import java.awt.*;

public class CourseServerPage extends OptionPage {

	private JTextField courseIDField;
	private JTextField serverIDField;

	public CourseServerPage() {
		super("Course and Server");
	}

	@Override
	protected void buildGUI() {

		Dimension labelSize = new Dimension(300, 28);

		Dimension minFieldSize = new Dimension(240, 28);
		Dimension fieldSize = new Dimension(300, 28);
		Dimension maxFieldSize = new Dimension(360, 28);

		setLayout(new MigLayout("", "[][grow]", "[][]"));

		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText(
				"""
				<html>Course and Server IDs for an Instance can't be changed after the
				Instance is created. If you'd like to create another Instance with
				the same configuration as this one, use the Copy Instance tool.</html>
				"""
		);
		descriptionLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(descriptionLabel, "cell 0 0 2 3");

		JLabel courseLabel = new JLabel("Course ID:");
//		courseLabel.setAlignmentX(LEFT_ALIGNMENT);
		courseLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
//		courseLabel.setPreferredSize(labelSize);
		add(courseLabel, "cell 0 4");

		courseIDField = new JTextField(24);
		courseIDField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
//		courseIDField.setMinimumSize(minFieldSize);
//		courseIDField.setPreferredSize(fieldSize);
//		courseIDField.setMaximumSize(maxFieldSize);
		courseIDField.setEditable(false);
		add(courseIDField, "cell 1 4");

		JLabel serverLabel = new JLabel("Server ID:");
//		serverLabel.setAlignmentX(LEFT_ALIGNMENT);
		serverLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
//		serverLabel.setPreferredSize(labelSize);
		add(serverLabel, "cell 0 6");

		serverIDField = new JTextField(24);
		serverIDField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
//		serverIDField.setMinimumSize(minFieldSize);
//		serverIDField.setPreferredSize(fieldSize);
//		serverIDField.setMaximumSize(maxFieldSize);
		serverIDField.setEditable(false);
		add(serverIDField, "cell 1 6");

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
