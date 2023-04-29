package org.canvacord.gui.dialog;

import org.canvacord.entity.ClassMeeting;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.util.CanvaCordModels;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.time.CanvaCordWeekdayWrapper;
import org.json.JSONObject;
import org.quartz.DateBuilder;

import javax.swing.*;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.Optional;

public class ClassMeetingDialog extends CanvaCordDialog {

	private static final int WIDTH = 320;
	private static final int HEIGHT = 240;

	// weeks start on Monday, fight me
	private final DayOfWeek[] weekdays = 	{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
											DayOfWeek.THURSDAY,	DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

	// choosing the weekday
	private JComboBox<CanvaCordWeekdayWrapper> weekdaySelector;

	// setting the start time
	private JSpinner startHourSpinner;
	private JSpinner startMinuteSpinner;
	private JRadioButton[] startAmPmButtons;

	// setting the end time
	private JSpinner endHourSpinner;
	private JSpinner endMinuteSpinner;
	private JRadioButton[] endAmPmButtons;

	// radio button controllers
	private ButtonGroup startGroup;
	private ButtonGroup endGroup;

	private ClassMeetingDialog() {
		super("Create Class Meeting", WIDTH, HEIGHT);
		buildGUI();
		initLogic();
	}

	private ClassMeetingDialog(ClassMeeting meetingToEdit) {
		this();
		prefillGUI(meetingToEdit);
	}

	@Override
	protected boolean verifyInputs() {
		// end time cannot be before start time
		int startHour = (Integer) startHourSpinner.getValue();
		int startMinute = (Integer) startMinuteSpinner.getValue();
		int endHour = (Integer) endHourSpinner.getValue();
		int endMinute = (Integer) endMinuteSpinner.getValue();
		if (startAmPmButtons[0].isSelected()) {
			if (startHour == 12) startHour = 0;
		}
		else {
			if (startHour != 12) startHour += 12;
		}
		if (endAmPmButtons[0].isSelected()) {
			if (endHour == 12) endHour = 0;
		}
		else {
			if (endHour != 12) endHour += 12;
		}

		Date startTime = DateBuilder.dateOf(startHour, startMinute, 0);
		Date endTime = DateBuilder.dateOf(endHour, endMinute, 0);

		if (endTime.before(startTime) || endTime.equals(startTime)) {
			UserInput.showErrorMessage("End time cannot be the same\nas or before start time.", "Start/End Mismatch");
			return false;
		}

		return true;
	}

	private void buildGUI() {

		// ================ POSITIONING ================
		final int labelX = 20;
		final int hoursX = 90;
		final int minutesX = 150;
		final int amPmX = 200;

		final int rowOneY = 20;
		final int rowTwoY = 60;
		final int rowThreeY = 100;

		// ================ SIZING ================
		final int labelWidth = 80;
		final int componentHeight = 28;
		final int spinnerWidth = 40;
		final int radioButtonWidth = 50;

		// ================ LABELS ================
		JLabel dayLabel = new JLabel("Weekday:");
		dayLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		dayLabel.setBounds(labelX, rowOneY, labelWidth, componentHeight);
		add(dayLabel);

		JLabel startsAt = new JLabel("Starts at:");
		startsAt.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		startsAt.setBounds(labelX, rowTwoY, labelWidth, componentHeight);
		add(startsAt);

		JLabel endsAt = new JLabel("Ends at:");
		endsAt.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		endsAt.setBounds(labelX, rowThreeY, labelWidth, componentHeight);
		add(endsAt);

		// ================ DAY SELECTOR ================
		weekdaySelector = new JComboBox<>();
		weekdaySelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weekdaySelector.setBounds(hoursX, rowOneY, 100, componentHeight);
		add(weekdaySelector);

		for (DayOfWeek weekday : weekdays)
			weekdaySelector.addItem(new CanvaCordWeekdayWrapper(weekday));

		// ================ HOUR SPINNERS ================
		startHourSpinner = new JSpinner();
		startHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		startHourSpinner.setBounds(hoursX, rowTwoY, spinnerWidth, componentHeight);
		startHourSpinner.setModel(CanvaCordModels.getHoursModel());
		add(startHourSpinner);

		endHourSpinner = new JSpinner();
		endHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		endHourSpinner.setBounds(hoursX, rowThreeY, spinnerWidth, componentHeight);
		endHourSpinner.setModel(CanvaCordModels.getHoursModel());
		add(endHourSpinner);

		// ================ MINUTE SPINNERS ================
		startMinuteSpinner = new JSpinner();
		startMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		startMinuteSpinner.setBounds(minutesX, rowTwoY, spinnerWidth, componentHeight);
		startMinuteSpinner.setModel(CanvaCordModels.getMinutesModel());
		startMinuteSpinner.setEditor(CanvaCordModels.getMinutesEditor(startMinuteSpinner));
		add(startMinuteSpinner);

		endMinuteSpinner = new JSpinner();
		endMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		endMinuteSpinner.setBounds(minutesX, rowThreeY, spinnerWidth, componentHeight);
		endMinuteSpinner.setModel(CanvaCordModels.getMinutesModel());
		endMinuteSpinner.setEditor(CanvaCordModels.getMinutesEditor(endMinuteSpinner));
		add(endMinuteSpinner);

		// ================ AM/PM ================
		startAmPmButtons = new JRadioButton[2];
		endAmPmButtons = new JRadioButton[2];

		startGroup = new ButtonGroup();
		endGroup = new ButtonGroup();

		for (int i = 0; i < 2; i++) {
			// build start buttons
			startAmPmButtons[i] = new JRadioButton(i == 0 ? "AM" : "PM");
			startAmPmButtons[i].setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			startAmPmButtons[i].setBounds(amPmX + i * (radioButtonWidth), rowTwoY, radioButtonWidth, componentHeight);
			startGroup.add(startAmPmButtons[i]);
			add(startAmPmButtons[i]);
			// build end buttons
			endAmPmButtons[i] = new JRadioButton(i == 0 ? "AM" : "PM");
			endAmPmButtons[i].setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			endAmPmButtons[i].setBounds(amPmX + i * (radioButtonWidth), rowThreeY, radioButtonWidth, componentHeight);
			endGroup.add(endAmPmButtons[i]);
			add(endAmPmButtons[i]);
		}

		// preselect AM
		startAmPmButtons[0].setSelected(true);
		endAmPmButtons[0].setSelected(true);

	}

	private void initLogic() {
		// TODO
	}

	private void prefillGUI(ClassMeeting meetingToEdit) {
		int weekdayPtr = 0;
		DayOfWeek weekday = meetingToEdit.getWeekday();
		while (weekdays[weekdayPtr] != weekday) weekdayPtr++;
		weekdaySelector.setSelectedIndex(weekdayPtr);
		JSONObject startTime = meetingToEdit.getStartTime();
		startHourSpinner.setValue(startTime.getInt("hour"));
		startMinuteSpinner.setValue(startTime.getInt("minute"));
		if (startTime.getString("ampm").equals("am")) startAmPmButtons[0].setSelected(true);
		else if (startTime.getString("ampm").equals("pm")) startAmPmButtons[1].setSelected(true);
		JSONObject endTime = meetingToEdit.getEndTime();
		endHourSpinner.setValue(endTime.getInt("hour"));
		endMinuteSpinner.setValue(endTime.getInt("minute"));
		if (endTime.getString("ampm").equals("am")) endAmPmButtons[0].setSelected(true);
		else if (endTime.getString("ampm").equals("pm")) endAmPmButtons[1].setSelected(true);
	}

	public Optional<ClassMeeting> getResult() {
		if (cancelled || !verifyInputs())
			return Optional.empty();
		else {
			DayOfWeek dayOfWeek = ((CanvaCordWeekdayWrapper) weekdaySelector.getSelectedItem()).weekday();
			JSONObject startTime = new JSONObject();
			startTime.put("hour", startHourSpinner.getValue());
			startTime.put("minute", startMinuteSpinner.getValue());
			startTime.put("ampm", startAmPmButtons[0].isSelected() ? "am" : startAmPmButtons[1].isSelected() ? "pm" : "");
			JSONObject endTime = new JSONObject();
			endTime.put("hour", endHourSpinner.getValue());
			endTime.put("minute", endMinuteSpinner.getValue());
			endTime.put("ampm", endAmPmButtons[0].isSelected() ? "am" : endAmPmButtons[1].isSelected() ? "pm" : "");
			return Optional.of(new ClassMeeting(dayOfWeek, startTime, endTime));
		}
	}

	public static Optional<ClassMeeting> buildClassMeeting() {
		ClassMeetingDialog dialog = new ClassMeetingDialog();
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

	// TODO edit mode
	public static Optional<ClassMeeting> editClassMeeting(ClassMeeting meetingToEdit) {
		ClassMeetingDialog dialog = new ClassMeetingDialog(meetingToEdit);
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

}
