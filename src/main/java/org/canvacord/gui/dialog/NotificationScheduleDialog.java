package org.canvacord.gui.dialog;

import org.canvacord.event.CanvaCordEvent;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class NotificationScheduleDialog extends CanvaCordDialog {

	private static final int WIDTH = 360;
	private static final int DUE_DATE_HEIGHT = 160;
	private static final int OTHER_HEIGHT = 240;

	private final CanvaCordEvent.Type eventType;

	// Multi-purpose components
	private JSpinner valueSpinner;
	private JComboBox<String> unitSelector;

	// Restricted components
	private JComboBox<String> typeSelector;

	private JSpinner dailyHourSpinner;
	private JSpinner dailyMinuteSpinner;
	private JRadioButton dailyAmButton;
	private JRadioButton dailyPmButton;
	private ButtonGroup dailyButtonGroup;

	private JCheckBox mondayBox;
	private JCheckBox tuesdayBox;
	private JCheckBox wednesdayBox;
	private JCheckBox thursdayBox;
	private JCheckBox fridayBox;
	private JCheckBox saturdayBox;
	private JCheckBox sundayBox;
	private JSpinner weeklyHourSpinner;
	private JSpinner weeklyMinuteSpinner;
	private JRadioButton weeklyAmButton;
	private JRadioButton weeklyPmButton;
	private ButtonGroup weeklyButtonGroup;

	private List<JComponent> intervalComponents;
	private List<JComponent> dailyComponents;
	private List<JComponent> weeklyComponents;

	private static final String INTERVALS = "At Intervals";
	private static final String DAILY = "Daily";
	private static final String WEEKLY = "Weekly";

	public NotificationScheduleDialog(CanvaCordEvent.Type eventType) {
		super("Notification Schedule", WIDTH, eventType == CanvaCordEvent.Type.ASSIGNMENT_DUE_DATE_APPROACHING ? DUE_DATE_HEIGHT : OTHER_HEIGHT);
		this.eventType = eventType;
		buildGUI();
		initLogic();
	}

	// TODO edit mode constructor

	@Override
	protected boolean verifyInputs() {
		// If the event type is an approaching due date, validate the "before" schedule
		if (eventType == CanvaCordEvent.Type.ASSIGNMENT_DUE_DATE_APPROACHING) {
			if (unitSelector.getSelectedItem() == null) {
				UserInput.showErrorMessage("Please select a time unit.", "Unit Error");
				return false;
			}
			return true;
		}
		else {
			// Check the type selection
			String type = (String) typeSelector.getSelectedItem();
			if (type == null) {
				UserInput.showErrorMessage("Please select a schedule type.", "Null Selection");
				return false;
			}
			// Branch on type selection
			switch ((String) typeSelector.getSelectedItem()) {
				// Nothing to validate for interval or daily types
				case INTERVALS, DAILY -> {
					return true;
				}
				// Weekly requires at least one day selected
				case WEEKLY -> {
					if (mondayBox.isSelected()) return true;
					if (tuesdayBox.isSelected()) return true;
					if (wednesdayBox.isSelected()) return true;
					if (thursdayBox.isSelected()) return true;
					if (fridayBox.isSelected()) return true;
					if (saturdayBox.isSelected()) return true;
					if (sundayBox.isSelected()) return true;
					UserInput.showErrorMessage("Please select at least one weekday.", "No Days Selected");
					return false;
				}
				default -> {
					return false;
				}
			}
		}
	}

	private void buildGUI() {

		final int spacing = 10;

		final int componentX = 20;
		final int topLabelY = 4;
		final int firstRowY = topLabelY + 30;
		final int secondRowY = firstRowY + 30;
		final int thirdRowY = secondRowY + 30;

		final int spinnerWidth = 60;
		final int unitSelectorWidth = 80;

		final int spinnerHeight = 24;

		// ================ UNIVERSAL CONFIGURATION ================
		JLabel scheduleLabel = new JLabel("Send Notifications:");
		scheduleLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		scheduleLabel.setBounds(componentX, topLabelY, 120, 24);
		add(scheduleLabel);

		valueSpinner = new JSpinner();
		valueSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		valueSpinner.setModel(new SpinnerNumberModel(1, 1, 23, 1));

		unitSelector = new JComboBox<>();
		unitSelector.addItem("Minutes");
		unitSelector.addItem("Hours");
		unitSelector.addItem("Days");
		unitSelector.setSelectedItem("Hours");
		unitSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);

		// ================ EVENT-SPECIFIC CONFIGURATION ================
		if (eventType == CanvaCordEvent.Type.ASSIGNMENT_DUE_DATE_APPROACHING) {

			final int dueDateUnitSelectorX = componentX + spinnerWidth + spacing;
			final int dueDatePostLabelX = dueDateUnitSelectorX + unitSelectorWidth + spacing;

			valueSpinner.setBounds(componentX, firstRowY, 60, spinnerHeight);
			add(valueSpinner);

			unitSelector.setBounds(dueDateUnitSelectorX, firstRowY, 80, spinnerHeight);
			add(unitSelector);

			JLabel postLabel = new JLabel("before the due date");
			postLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
			postLabel.setBounds(dueDatePostLabelX, firstRowY, 200, 24);
			add(postLabel);

		}
		else {

			final int radioButtonWidth = 50;
			final int timeSpinnerWidth = 40;
			final int checkBoxWidth = 44;

			final int intervalSpinnerX = 110;
			final int intervalUnitSelectorX = intervalSpinnerX + spinnerWidth + spacing;

			final int dailyHourSpinnerX = 110;
			final int dailyMinuteSpinnerX = dailyHourSpinnerX + timeSpinnerWidth + spacing;
			final int dailyAmX = dailyMinuteSpinnerX + timeSpinnerWidth + spacing;
			final int dailyPmX = dailyAmX + radioButtonWidth;

			final int weeklyEveryLabelWidth = 40;
			final int checkBoxX = componentX;

			final int weeklyHourSpinnerX = componentX + 50;
			final int weeklyMinuteSpinnerX = weeklyHourSpinnerX + timeSpinnerWidth + spacing;
			final int weeklyAmX = weeklyMinuteSpinnerX + timeSpinnerWidth + spacing;
			final int weeklyPmX = weeklyAmX + radioButtonWidth;

			// ================ SELECT SCHEDULE TYPE ================
			typeSelector = new JComboBox<>();
			typeSelector.addItem(INTERVALS);
			typeSelector.addItem(DAILY);
			typeSelector.addItem(WEEKLY);
			typeSelector.setSelectedItem(INTERVALS);
			typeSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			typeSelector.setBounds(150, topLabelY, 100, spinnerHeight);
			add(typeSelector);

			// ================ INTERVAL TYPE COMPONENTS ================
			intervalComponents = new ArrayList<>();

			JLabel everyLabel = new JLabel("Every");
			everyLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
			everyLabel.setBounds(componentX, secondRowY, 80, 24);
			intervalComponents.add(everyLabel);

			valueSpinner.setBounds(intervalSpinnerX, secondRowY, spinnerWidth, spinnerHeight);
			intervalComponents.add(valueSpinner);

			unitSelector.setBounds(intervalUnitSelectorX, secondRowY, 80, spinnerHeight);
			intervalComponents.add(unitSelector);

			// ================ DAILY TYPE COMPONENTS ================
			dailyComponents = new ArrayList<>();

			JLabel dailyLabel = new JLabel("Every day at");
			dailyLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
			dailyLabel.setBounds(componentX, secondRowY, 100, 24);
			dailyComponents.add(dailyLabel);

			dailyHourSpinner = new JSpinner();
			dailyHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			dailyHourSpinner.setBounds(dailyHourSpinnerX, secondRowY, timeSpinnerWidth, spinnerHeight);
			dailyHourSpinner.setModel(new SpinnerNumberModel(12, 1, 12, 1));
			dailyComponents.add(dailyHourSpinner);

			dailyMinuteSpinner = new JSpinner();
			dailyMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			dailyMinuteSpinner.setBounds(dailyMinuteSpinnerX, secondRowY, timeSpinnerWidth, spinnerHeight);
			dailyMinuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
			dailyMinuteSpinner.setEditor(new JSpinner.NumberEditor(dailyMinuteSpinner, "00"));
			dailyComponents.add(dailyMinuteSpinner);

			dailyAmButton = new JRadioButton("AM");
			dailyAmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			dailyAmButton.setBounds(dailyAmX, secondRowY, radioButtonWidth, 24);
			dailyComponents.add(dailyAmButton);

			dailyPmButton = new JRadioButton("PM");
			dailyPmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			dailyPmButton.setBounds(dailyPmX, secondRowY, radioButtonWidth, 24);
			dailyComponents.add(dailyPmButton);

			dailyButtonGroup = new ButtonGroup();
			dailyButtonGroup.add(dailyAmButton);
			dailyButtonGroup.add(dailyPmButton);

			// ================ WEEKLY TYPE COMPONENTS ================
			weeklyComponents = new ArrayList<>();

			JLabel weeklyEveryLabel = new JLabel("Every:");
			weeklyEveryLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
			weeklyEveryLabel.setBounds(componentX, firstRowY + 4, weeklyEveryLabelWidth, 24);
			weeklyComponents.add(weeklyEveryLabel);

			mondayBox = new JCheckBox("Mo");
			tuesdayBox = new JCheckBox("Tu");
			wednesdayBox = new JCheckBox("We");
			thursdayBox = new JCheckBox("Th");
			fridayBox = new JCheckBox("Fr");
			saturdayBox = new JCheckBox("Sa");
			sundayBox = new JCheckBox("Su");

			mondayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			tuesdayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			wednesdayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			thursdayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			fridayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			saturdayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			sundayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);

			mondayBox.setBounds(checkBoxX, secondRowY, checkBoxWidth, 24);
			tuesdayBox.setBounds(checkBoxX + checkBoxWidth, secondRowY, checkBoxWidth, 24);
			wednesdayBox.setBounds(checkBoxX + checkBoxWidth * 2, secondRowY, checkBoxWidth, 24);
			thursdayBox.setBounds(checkBoxX + checkBoxWidth * 3, secondRowY, checkBoxWidth, 24);
			fridayBox.setBounds(checkBoxX + checkBoxWidth * 4, secondRowY, checkBoxWidth, 24);
			saturdayBox.setBounds(checkBoxX + checkBoxWidth * 5, secondRowY, checkBoxWidth, 24);
			sundayBox.setBounds(checkBoxX + checkBoxWidth * 6, secondRowY, checkBoxWidth, 24);

			weeklyComponents.add(mondayBox);
			weeklyComponents.add(tuesdayBox);
			weeklyComponents.add(wednesdayBox);
			weeklyComponents.add(thursdayBox);
			weeklyComponents.add(fridayBox);
			weeklyComponents.add(saturdayBox);
			weeklyComponents.add(sundayBox);

			JLabel atLabel = new JLabel("At:");
			atLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
			atLabel.setBounds(componentX, thirdRowY, 28, 24);
			weeklyComponents.add(atLabel);

			weeklyHourSpinner = new JSpinner();
			weeklyHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			weeklyHourSpinner.setBounds(weeklyHourSpinnerX, thirdRowY, timeSpinnerWidth, spinnerHeight);
			weeklyHourSpinner.setModel(new SpinnerNumberModel(12, 1, 12, 1));
			weeklyComponents.add(weeklyHourSpinner);

			weeklyMinuteSpinner = new JSpinner();
			weeklyMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			weeklyMinuteSpinner.setBounds(weeklyMinuteSpinnerX, thirdRowY, timeSpinnerWidth, spinnerHeight);
			weeklyMinuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
			weeklyMinuteSpinner.setEditor(new JSpinner.NumberEditor(weeklyMinuteSpinner, "00"));
			weeklyComponents.add(weeklyMinuteSpinner);

			weeklyAmButton = new JRadioButton("AM");
			weeklyAmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			weeklyAmButton.setBounds(weeklyAmX, thirdRowY, radioButtonWidth, 24);
			weeklyComponents.add(weeklyAmButton);

			weeklyPmButton = new JRadioButton("PM");
			weeklyPmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			weeklyPmButton.setBounds(weeklyPmX, thirdRowY, radioButtonWidth, 24);
			weeklyComponents.add(weeklyPmButton);

			weeklyButtonGroup = new ButtonGroup();
			weeklyButtonGroup.add(weeklyAmButton);
			weeklyButtonGroup.add(weeklyPmButton);

			// ================ INITIAL SELECTION ================
			displayComponentsForCurrentMode(INTERVALS);

		}

	}

	private void initLogic() {
		if (eventType != CanvaCordEvent.Type.ASSIGNMENT_DUE_DATE_APPROACHING) {
			typeSelector.addItemListener(selection -> {
				displayComponentsForCurrentMode((String) selection.getItem());
			});
			unitSelector.addItemListener(selection -> {
				switch ((String) selection.getItem()) {
					case "Minutes" -> valueSpinner.setModel(new SpinnerNumberModel(1, 1, 59, 1));
					case "Hours" -> valueSpinner.setModel(new SpinnerNumberModel(1, 1, 23, 1));
					case "Days" -> valueSpinner.setModel(new SpinnerNumberModel(1, 1, 99, 1));
				}
			});
		}
	}

	private void displayComponentsForCurrentMode(String selection) {
		switch (selection) {
			case INTERVALS -> {
				removeAllComponents(dailyComponents);
				removeAllComponents(weeklyComponents);
				addAllComponents(intervalComponents);
			}
			case DAILY -> {
				removeAllComponents(intervalComponents);
				removeAllComponents(weeklyComponents);
				addAllComponents(dailyComponents);
			}
			case WEEKLY -> {
				removeAllComponents(intervalComponents);
				removeAllComponents(dailyComponents);
				addAllComponents(weeklyComponents);
			}
			default -> throw new CanvaCordException("Invalid notif schedule mode selection");
		}
		// show the changes
		repaint();
	}

	private void addAllComponents(List<JComponent> components) {
		for (JComponent component : components) {
			add(component);
			component.revalidate();
			component.repaint();
		}
	}

	private void removeAllComponents(List<JComponent> components) {
		for (JComponent component : components) {
			remove(component);
		}
	}

	public Optional<JSONObject> getResult() {
		if (cancelled || !verifyInputs())
			return Optional.empty();
		else {
			JSONObject result = new JSONObject();
			if (eventType == CanvaCordEvent.Type.ASSIGNMENT_DUE_DATE_APPROACHING) {
				result.put("unit", ((String) unitSelector.getSelectedItem()).toLowerCase());
				result.put("value", valueSpinner.getValue());
			}
			else {
				switch ((String) Objects.requireNonNull(typeSelector.getSelectedItem())) {
					case INTERVALS -> {
						result.put("type", "interval");
						JSONObject interval = new JSONObject();
						interval.put("unit", ((String) unitSelector.getSelectedItem()).toLowerCase());
						interval.put("value", valueSpinner.getValue());
						result.put("interval", interval);
					}
					case DAILY -> {
						result.put("type", "daily");
						JSONObject time = new JSONObject();
						time.put("hour", dailyHourSpinner.getValue());
						time.put("minute", dailyMinuteSpinner.getValue());
						time.put("ampm", dailyAmButton.isSelected() ? "am" : dailyPmButton.isSelected() ? "pm" : "");
						result.put("time", time);
					}
					case WEEKLY -> {
						result.put("type", "weekly");
						JSONArray days = new JSONArray();
						if (mondayBox.isSelected()) days.put("Mon");
						if (tuesdayBox.isSelected()) days.put("Tue");
						if (wednesdayBox.isSelected()) days.put("Wed");
						if (thursdayBox.isSelected()) days.put("Thu");
						if (fridayBox.isSelected()) days.put("Fri");
						if (saturdayBox.isSelected()) days.put("Sat");
						if (sundayBox.isSelected()) days.put("Sun");
						result.put("days", days);
						JSONObject time = new JSONObject();
						time.put("hour", weeklyHourSpinner.getValue());
						time.put("minute", weeklyMinuteSpinner.getValue());
						time.put("ampm", weeklyAmButton.isSelected() ? "am" : weeklyPmButton.isSelected() ? "pm" : "");
						result.put("time", time);
					}
					default -> {
						throw new CanvaCordException("Invalid notification schedule type");
					}
				}
			}
			return Optional.of(result);
		}
	}

	public static Optional<JSONObject> buildNotificationSchedule(CanvaCordEvent.Type eventType) {
		NotificationScheduleDialog dialog = new NotificationScheduleDialog(eventType);
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

}
