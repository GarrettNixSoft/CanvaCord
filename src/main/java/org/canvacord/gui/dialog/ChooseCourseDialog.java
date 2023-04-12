package org.canvacord.gui.dialog;

import edu.ksu.canvas.model.Course;
import org.canvacord.canvas.CanvasApi;
import org.canvacord.entity.CourseWrapper;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.persist.ConfigManager;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.time.LongTask;
import org.canvacord.util.time.LongTaskDialog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChooseCourseDialog extends CanvaCordDialog {

	private static final int WIDTH = 400;
	private static final int HEIGHT = 150;

	private static final List<Course> canvasCourses = new ArrayList<>();

	private JComboBox<CourseWrapper> courseSelector;

	public ChooseCourseDialog() {
		super("Choose Course", WIDTH, HEIGHT);
		buildGUI();
	}

	@Override
	protected boolean verifyInputs() {

		if (courseSelector.getItemCount() == 0) {
			UserInput.showErrorMessage("Failed to load Canvas courses.\nPlease check your Canvas account\nand access token.", "Access Error");
			return false;
		}
		if (courseSelector.getSelectedItem() == null) {
			UserInput.showErrorMessage("You must select a course.", "No Selection");
			return false;
		}

		return true;
	}

	private void buildGUI() {

		final int componentX = 20;
		final int labelY = 4;
		final int selectorY = 36;

		JLabel label = new JLabel("Choose a Canvas Course:");
		label.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		label.setBounds(componentX, labelY, WIDTH - componentX * 3, 28);
		add(label);

		courseSelector = new JComboBox<>();
		courseSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		courseSelector.setBounds(componentX, selectorY, WIDTH - componentX * 3, 28);
		add(courseSelector);

		pack();

		SwingUtilities.invokeLater(() -> {
			if (canvasCourses.isEmpty()) {
				LongTask fetchCourses = () -> canvasCourses.addAll(CanvasApi.getInstance().getCourses(ConfigManager.getUserID()));
				LongTaskDialog.runLongTask(fetchCourses, "Fetching Canvas Courses...", "Fetch");
			}
			if (canvasCourses.isEmpty()) {
				UserInput.showWarningMessage("Failed to load Canvas courses.\nPlease check your Canvas account\nand access token.", "Access Error");
			}
			else for (Course course : canvasCourses) {
				if (!(course.getName() == null || course.getName().isBlank())) {
					courseSelector.addItem(new CourseWrapper(course));
				}
			}
		});

	}

	public Optional<Course> getResult() {
		if (cancelled)
			return Optional.empty();
		else {
			return Optional.of(((CourseWrapper) courseSelector.getSelectedItem()).course());
		}
	}

	public static Optional<Course> chooseCourse() {
		ChooseCourseDialog dialog = new ChooseCourseDialog();
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

}
