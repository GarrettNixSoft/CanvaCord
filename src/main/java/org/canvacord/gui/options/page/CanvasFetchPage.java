package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.entity.CanvasFetchScheduleType;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.exception.CronException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.main.CanvaCord;
import org.canvacord.util.CanvaCordModels;
import org.canvacord.util.string.StringUtils;
import org.canvacord.util.time.CanvaCordTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.CronExpression;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.canvacord.entity.CanvasFetchScheduleType.*;

public class CanvasFetchPage extends OptionPage {

	private JSONObject fetchSchedule;

	private JComboBox<String> scheduleTypeSelector;
	private Map<String, List<JComponent>> componentLists;
	private String previousScheduleType;

	private Map<JComponent, String> layoutStrings;

	// frequent components
	private JSpinner frequentValueSpinner;

	// hourly components
	private JSpinner hourlyValueSpinner;

	// daily components
	private JSpinner dailyHourSpinner;
	private JSpinner dailyMinuteSpinner;
	private JRadioButton dailyAmButton;
	private JRadioButton dailyPmButton;

	// weekly components
	private JCheckBox[] weekdayButtons;
	private JSpinner weeklyHourSpinner;
	private JSpinner weeklyMinuteSpinner;
	private JRadioButton weeklyAmButton;
	private JRadioButton weeklyPmButton;

	// cron components
	private JTextField cronField;

	public CanvasFetchPage() {
		super("Canvas Fetching");
	}

	@Override
	protected void buildGUI() {

		// prepare layout and collections
		setLayout(new MigLayout("", "[][][][][]", "[][][]"));
		componentLists = new HashMap<>();
		layoutStrings = new HashMap<>();
		previousScheduleType = "";

		// ================ SELECTING SCHEDULE TYPE ================
		JPanel scheduleSelectPanel = new JPanel();
		scheduleSelectPanel.setLayout(new MigLayout("", "[grow]", "[]"));

		JLabel scheduleTypeLabel = new JLabel("Schedule type:");
		scheduleTypeLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		scheduleSelectPanel.add(scheduleTypeLabel, "cell 0 0");

		scheduleTypeSelector = new JComboBox<>();
		scheduleTypeSelector.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		scheduleSelectPanel.add(scheduleTypeSelector, "cell 1 0");

		add(scheduleSelectPanel, "span");

		for (String type : CanvasFetchScheduleType.TYPES) {
			scheduleTypeSelector.addItem(StringUtils.uppercaseWords(type));
			componentLists.put(type, new ArrayList<>());
		}

		// ================ FREQUENT COMPONENTS ================
		JPanel frequentPanel = new JPanel();
		frequentPanel.setLayout(new MigLayout("", "[][][]", "[]"));

		JLabel frequentEveryLabel = new JLabel("Every:");
		frequentEveryLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		frequentPanel.add(frequentEveryLabel, "cell 0 0");

		frequentValueSpinner = new JSpinner();
		frequentValueSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		frequentValueSpinner.setModel(CanvaCordModels.getMinutesModel());
		frequentPanel.add(frequentValueSpinner, "cell 1 0");

		JLabel minutesLabel = new JLabel("Minute(s)");
		minutesLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		frequentPanel.add(minutesLabel, "cell 2 0");

		componentLists.get(FREQUENT).add(frequentPanel);
		layoutStrings.put(frequentPanel, "cell 0 2");

		// ================ HOURLY COMPONENTS ================
		JPanel hourlyPanel = new JPanel();
		hourlyPanel.setLayout(new MigLayout("", "[][][]", "[]"));

		JLabel hourlyEveryLabel = new JLabel("Every:");
		hourlyEveryLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		hourlyPanel.add(hourlyEveryLabel, "cell 0 0");

		hourlyValueSpinner = new JSpinner();
		hourlyValueSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		hourlyValueSpinner.setModel(CanvaCordModels.getHoursModel());
		hourlyPanel.add(hourlyValueSpinner, "cell 1 0");

		JLabel hoursLabel = new JLabel("Hour(s)");
		hoursLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		hourlyPanel.add(hoursLabel, "cell 2 0");

		componentLists.get(HOURLY).add(hourlyPanel);
		layoutStrings.put(hourlyPanel, "cell 0 2");

		// ================ DAILY COMPONENTS ================
		JPanel dailyTimePanel = new JPanel();
		dailyTimePanel.setLayout(new MigLayout("", "[][][][][]", "[]"));

		JLabel preLabel = new JLabel("Every day at:");
		preLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		dailyTimePanel.add(preLabel, "cell 0 0");

		dailyMinuteSpinner = new JSpinner();
		dailyMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyMinuteSpinner.setModel(CanvaCordModels.getMinutesModel());
		dailyMinuteSpinner.setEditor(CanvaCordModels.getMinutesEditor(dailyMinuteSpinner));
		dailyTimePanel.add(dailyMinuteSpinner, "cell 2 0");

		dailyHourSpinner = new JSpinner();
		dailyHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyHourSpinner.setModel(CanvaCordModels.getHoursModel());
		dailyTimePanel.add(dailyHourSpinner, "cell 1 0");

		dailyAmButton = new JRadioButton("AM");
		dailyAmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyTimePanel.add(dailyAmButton, "cell 3 0");

		dailyPmButton = new JRadioButton("PM");
		dailyPmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyTimePanel.add(dailyPmButton, "cell 4 0");

		componentLists.get(DAILY).add(dailyTimePanel);
		layoutStrings.put(dailyTimePanel, "cell 0 2");

		ButtonGroup dailyButtonGroup = new ButtonGroup();
		dailyButtonGroup.add(dailyAmButton);
		dailyButtonGroup.add(dailyPmButton);

		// default AM selection
		dailyAmButton.setSelected(true);

		// ================ WEEKLY COMPONENTS ================
		JPanel weekdaysPanel = new JPanel();
		weekdaysPanel.setLayout(new MigLayout("", "[][][][][][][][]", "[]"));

		JLabel daysLabel = new JLabel("Days:");
		daysLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		weekdaysPanel.add(daysLabel, "cell 0 0");

		weekdayButtons = new JCheckBox[7];
		for (int i = 0; i < weekdayButtons.length; i++) {
			JCheckBox checkBox = new JCheckBox(CanvaCordTime.WEEKDAY_ABBREV[i]);
			checkBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			weekdaysPanel.add(checkBox, "cell " + i + " 0");
			weekdayButtons[i] = checkBox;
		}

		componentLists.get(WEEKLY).add(weekdaysPanel);
		layoutStrings.put(weekdaysPanel, "cell 0 2 8 1");

		JPanel weeklyTimePanel = new JPanel();
		weeklyTimePanel.setLayout(new MigLayout("", "[][][][][]", "[]"));

		JLabel atLabel = new JLabel("At:");
		atLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		weeklyTimePanel.add(atLabel, "cell 0 0");

		weeklyHourSpinner = new JSpinner();
		weeklyHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyHourSpinner.setModel(CanvaCordModels.getHoursModel());
		weeklyTimePanel.add(weeklyHourSpinner, "cell 1 0");

		weeklyMinuteSpinner = new JSpinner();
		weeklyMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyMinuteSpinner.setModel(CanvaCordModels.getMinutesModel());
		weeklyMinuteSpinner.setEditor(CanvaCordModels.getMinutesEditor(weeklyMinuteSpinner));
		weeklyTimePanel.add(weeklyMinuteSpinner, "cell 2 0");

		weeklyAmButton = new JRadioButton("AM");
		weeklyAmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyTimePanel.add(weeklyAmButton, "cell 3 0");

		weeklyPmButton = new JRadioButton("PM");
		weeklyPmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyTimePanel.add(weeklyPmButton, "cell 4 0");

		componentLists.get(WEEKLY).add(weeklyTimePanel);
		layoutStrings.put(weeklyTimePanel, "cell 0 4 5 1");

		ButtonGroup weeklyButtonGroup = new ButtonGroup();
		weeklyButtonGroup.add(weeklyAmButton);
		weeklyButtonGroup.add(weeklyPmButton);

		// default AM selection
		weeklyAmButton.setSelected(true);

		// ================ CRON COMPONENTS ================
		JPanel cronPanel = new JPanel();
		cronPanel.setLayout(new MigLayout("", "[][]", "[]"));

		JLabel cronLabel = new JLabel("Enter Cron String:");
		cronLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		cronPanel.add(cronLabel, "cell 0 0");

		cronField = new JTextField(24);
		cronField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		cronPanel.add(cronField, "cell 1 0");

		componentLists.get(CRON).add(cronPanel);
		layoutStrings.put(cronPanel, "cell 0 2");

		// TODO dynamically select starting schedule type
		// for now, just default to frequent
		updateScheduleGUI(FREQUENT);

	}

	@Override
	protected void initLogic() {
		scheduleTypeSelector.addItemListener(selection -> {
			String scheduleType = (String) selection.getItem();
			updateScheduleGUI(scheduleType);
		});
	}

	@Override
	protected void prefillGUI() {

		fetchSchedule = (JSONObject) dataStore.get("fetch_schedule");
		String type = fetchSchedule.getString("type");

		switch (type) {
			case "interval" -> {
				JSONObject interval = fetchSchedule.getJSONObject("interval");
				String unit = interval.getString("unit");
				int value = interval.getInt("value");
				if (unit.equals("minutes")) {
					scheduleTypeSelector.setSelectedIndex(0);
					frequentValueSpinner.setValue(value);
					updateScheduleGUI(FREQUENT);
				}
				else if (unit.equals("hours")) {
					scheduleTypeSelector.setSelectedIndex(1);
					frequentValueSpinner.setValue(value);
					updateScheduleGUI(HOURLY);
				}
				else {
					CanvaCord.explode("Invalid Canvas Fetch interval unit type: " + type);
				}
			}
			case DAILY -> {
				dailyHourSpinner.setValue(fetchSchedule.getInt("hour"));
				dailyMinuteSpinner.setValue(fetchSchedule.getInt("minute"));
				String ampm = fetchSchedule.getString("ampm");
				if (ampm.equals("am"))
					dailyAmButton.setSelected(true);
				else if (ampm.equals("pm"))
					dailyPmButton.setSelected(true);
				scheduleTypeSelector.setSelectedIndex(2);
				updateScheduleGUI(DAILY);
			}
			case WEEKLY -> {
				weeklyHourSpinner.setValue(fetchSchedule.getInt("hour"));
				weeklyMinuteSpinner.setValue(fetchSchedule.getInt("minute"));
				String ampm = fetchSchedule.getString("ampm");
				if (ampm.equals("am"))
					weeklyAmButton.setSelected(true);
				else if (ampm.equals("pm"))
					weeklyPmButton.setSelected(true);
				JSONArray days = fetchSchedule.getJSONArray("days");
				int buttonPtr = 0;
				for (int i = 0; i < days.length(); i++) {
					String day = days.getString(i);
					while (!weekdayButtons[buttonPtr].getText().toLowerCase().equals(day))
						buttonPtr++;
					weekdayButtons[buttonPtr].setSelected(true);
				}
				scheduleTypeSelector.setSelectedIndex(3);
				updateScheduleGUI(WEEKLY);
			}
			case CRON -> {
				cronField.setText(fetchSchedule.getString("cron"));
				scheduleTypeSelector.setSelectedIndex(4);
				updateScheduleGUI(CRON);
			}
		}

		System.out.println("selected: " + scheduleTypeSelector.getSelectedItem());

	}

	@Override
	protected void verifyInputs() throws Exception {

		Object rawSelection = scheduleTypeSelector.getSelectedItem();
		if (rawSelection == null) {
			throw new CanvaCordException("No schedule type selected");
		}

		String selection = (String) rawSelection;

		if (selection.equals(CRON)) {
			if (!CronExpression.isValidExpression(cronField.getText()))
				throw new CronException("Invalid Cron expression");
		}

		// Save the schedule
		dataStore.store("fetch_schedule", getScheduleJSON());

	}

	private void updateScheduleGUI(String newScheduleType) {
		// lower case the parameter
		newScheduleType = newScheduleType.toLowerCase();
		// Check for no change
		if (newScheduleType.equals(previousScheduleType)) return;
		// Remove all components for the previous type (if there was one)
		if (!previousScheduleType.isBlank()) {
			for (JComponent component : componentLists.get(previousScheduleType)) {
				remove(component);
			}
		}
		// Add all components for the new type
		for (JComponent component : componentLists.get(newScheduleType)) {
			String layoutString = layoutStrings.get(component);
			add(component, layoutString);
		}
		// Assign previous to the new type
		previousScheduleType = newScheduleType;
		// show the changes
		SwingUtilities.invokeLater(() -> {
			revalidate();
			repaint();
		});
	}

	private JSONObject getScheduleJSON() {

		if (scheduleTypeSelector.getSelectedItem() == null)
			throw new CanvaCordException("No fetch schedule type selected");

		// Build a JSON representation of the user's schedule based on what they input in the GUI fields
		fetchSchedule = new JSONObject();

		switch (((String) scheduleTypeSelector.getSelectedItem()).toLowerCase()) {
			case FREQUENT -> {
				fetchSchedule.put("type", "interval");
				fetchSchedule.put("round", false);
				JSONObject interval = new JSONObject();
				interval.put("unit", "minutes");
				interval.put("value", frequentValueSpinner.getValue());
				fetchSchedule.put("interval", interval);
			}
			case HOURLY -> {
				fetchSchedule.put("type", "interval");
				fetchSchedule.put("round", true);
				JSONObject interval = new JSONObject();
				interval.put("unit", "hours");
				interval.put("amount", hourlyValueSpinner.getValue());
				fetchSchedule.put("interval", interval);
			}
			case DAILY -> {
				fetchSchedule.put("type", "daily");
				fetchSchedule.put("hour", dailyHourSpinner.getValue());
				fetchSchedule.put("minute", dailyMinuteSpinner.getValue());
				fetchSchedule.put("ampm", dailyAmButton.isSelected() ? "am" : dailyPmButton.isSelected() ? "pm" : "");
			}
			case WEEKLY -> {
				fetchSchedule.put("type", "weekly");
				fetchSchedule.put("hour", weeklyHourSpinner.getValue());
				fetchSchedule.put("minute", weeklyMinuteSpinner.getValue());
				fetchSchedule.put("ampm", weeklyAmButton.isSelected() ? "am" : weeklyPmButton.isSelected() ? "pm" : "");
				JSONArray days = new JSONArray();
				for (JCheckBox dayCheckbox : weekdayButtons) {
					if (dayCheckbox.isSelected())
						days.put(dayCheckbox.getText().toLowerCase());
				}
				fetchSchedule.put("days", days);
			}
			case CRON -> {
				fetchSchedule.put("type", "cron");
				fetchSchedule.put("cron", cronField.getText());
			}
			default -> {
				throw new CanvaCordException("what");
			}
		}

		return fetchSchedule;

	}

}
