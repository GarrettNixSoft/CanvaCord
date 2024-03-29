package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.instance.Instance;
import org.canvacord.util.gui.ComponentUtils;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class InstanceCanvasFetchCard extends InstanceConfigCard {

	// ================ ROW PANELS ================
	private JPanel frequentPanel;
	private JPanel hourlyPanel;
	private JPanel dailyPanel;
	private JPanel weeklyPanel;
	private JPanel customPanel;

	// ================ SELECTING ROWS ================
	private ButtonGroup radioButtonGroup;
	private JRadioButton frequentButton;
	private JRadioButton hourlyButton;
	private JRadioButton dailyButton;
	private JRadioButton weeklyButton;
	private JRadioButton customButton;

	// ================ USER INPUTS ================
	// ======== FREQUENT ========
	private JSpinner freqMinuteSpinner;
	// ======== HOURLY ========
	private JSpinner hourlyHoursSpinner;
	// ======== DAILY ========
	private JSpinner dailyHourSpinner;
	private JSpinner dailyMinuteSpinner;
	private JRadioButton dailyAmButton;
	private JRadioButton dailyPmButton;
	// ======== WEEKLY ========
	private List<JCheckBox> dayCheckboxes;
	private JSpinner weeklyHourSpinner;
	private JSpinner weeklyMinuteSpinner;
	private JRadioButton weeklyAmButton;
	private JRadioButton weeklyPmButton;
	// ======== CUSTOM ========
	private JTextField customCronField;

	// ================ CONTENT SPACING ================
	private static final int CONTENT_PADDING_HORIZONTAL = 50;
	private static final int CONTENT_PADDING_VERTICAL = 24;
	private static final int HEADER_SPACING = 36;
	private static final int ROW_SPACING = 20;

	private static final int SPINNER_WIDTH = 40;
	private static final int SPINNER_HEIGHT = 24;

	public InstanceCanvasFetchCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Configure Fetch Schedule");
	}

	@Override
	protected void buildGUI() {

		// ================ CONTENT LAYOUT ================
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(new EmptyBorder(CONTENT_PADDING_VERTICAL, CONTENT_PADDING_VERTICAL, CONTENT_PADDING_HORIZONTAL, CONTENT_PADDING_HORIZONTAL));

		radioButtonGroup = new ButtonGroup();

		// ================ HEADER LABEL ================
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

		JLabel headerLabel = new JLabel("Choose when to check Canvas for updates:");
		headerLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		headerPanel.add(headerLabel);

		contentPanel.add(headerPanel);
		contentPanel.add(Box.createVerticalStrut(HEADER_SPACING));

		// ================ SCHEDULE CATEGORIES ================
		// ======== FREQUENT ========
		buildFrequentPanel();
		// ======== HOURLY ========
		buildHourlyPanel();
		// ======== DAILY ========
		buildDailyPanel();
		// ======== WEEKLY ========
		buildWeeklyPanel();
		// ======== CUSTOM ========
		buildCustomPanel();

		// ================ CONTROL AVAILABLE COMPONENTS ================
		updatePanels();

	}

	@Override
	protected void initLogic() {

		// Whenever a radio button for a schedule type is clicked, activate that row and disable all the other rows
		ActionListener typeSelectionListener = event -> {
			updatePanels();
		};

		// instruct every radio button for each row to perform this action when selected
		frequentButton.addActionListener(typeSelectionListener);
		hourlyButton.addActionListener(typeSelectionListener);
		dailyButton.addActionListener(typeSelectionListener);
		weeklyButton.addActionListener(typeSelectionListener);
		customButton.addActionListener(typeSelectionListener);

	}

	@Override
	/**
	 * Prefills the CanvasFetchCard
	 * Andrew Bae
	 */
	public void prefillGUI(Instance instanceToEdit) {
		// TODO Andrew
		JSONObject previousSchedule = instanceToEdit.getCanvasFetchSchedule();
		//System.out.println(previousSchedule.getString("type"));
		try {
			//Checks if the minutespinner
			if (previousSchedule.getString("type").equals("interval")) {
				if (previousSchedule.getBoolean("round")) {
					frequentButton.setSelected(true);
					freqMinuteSpinner.setValue(previousSchedule.getJSONObject("interval").get("amount"));
				} else if (!previousSchedule.getBoolean("round")) {
					hourlyButton.setSelected(true);
					hourlyHoursSpinner.setValue(previousSchedule.getJSONObject("interval").get("amount"));
				}
				//Checks if daily is checked
			} else if (previousSchedule.getString("type").equals("daily")) {
				dailyButton.setSelected(true);
				dailyHourSpinner.setValue(previousSchedule.get("hour"));
				dailyMinuteSpinner.setValue(previousSchedule.get("minute"));
				if (previousSchedule.getString("ampm").equals("am")) {
					dailyAmButton.setSelected(true);
				} else if (previousSchedule.getString("ampm").equals("pm")) {
					dailyPmButton.setSelected(true);
				}
				//checks if weekly is checked
			} else if (previousSchedule.getString("type").equals("weekly")) {
				//System.out.println("Check Flag: WOrks");
				weeklyButton.setSelected(true);
				weeklyHourSpinner.setValue(previousSchedule.get("hour"));
				weeklyMinuteSpinner.setValue(previousSchedule.get("minute"));
				if (previousSchedule.getString("ampm").equals("am")) {
					weeklyAmButton.setSelected(true);
				} else if (previousSchedule.getString("ampm").equals("pm")) {
					weeklyPmButton.setSelected(true);
				}
				JSONArray days = previousSchedule.getJSONArray("days");
				for (int x = 0; x < days.length(); x++) {
					for (JCheckBox dayCheckbox : dayCheckboxes) {
						if (dayCheckbox.getText().toLowerCase().equals(days.getString(x).toLowerCase())) {
							dayCheckbox.setSelected(true);
						}
					}
				}
				//Checks if custom is checked
			} else if (previousSchedule.getString("type").equals("cron")) {
				customButton.setSelected(true);
				customCronField.setText(previousSchedule.getString("cron"));
				//Checks if there is an error
			} else {
				System.out.println("Error");
			}
		} catch(Exception e) {
			UserInput.showExceptionWarning(e);
		}
		updatePanels();
	}

	private void buildFrequentPanel() {

		// ================ PANEL ================
		frequentPanel = new JPanel();
		frequentPanel.setLayout(new BoxLayout(frequentPanel, BoxLayout.X_AXIS));

		// ================ RADIO BUTTON ================
		frequentButton = new JRadioButton("Frequent");
		frequentButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		frequentPanel.add(frequentButton);

		frequentPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(frequentButton);

		// ================ SELECT FREQUENT BY DEFAULT ================
		frequentButton.setSelected(true);

		// ================ PRE-SPINNER LABEL ================
		JLabel everyLabel = new JLabel("Every");
		everyLabel.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		frequentPanel.add(everyLabel);

		frequentPanel.add(Box.createHorizontalStrut(4));

		// ================ SPINNER + MODEL ================
		freqMinuteSpinner = new JSpinner();
		freqMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		freqMinuteSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		freqMinuteSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		freqMinuteSpinner.setModel(new SpinnerNumberModel(15, 1, 59, 1));
		frequentPanel.add(freqMinuteSpinner);

		frequentPanel.add(Box.createHorizontalStrut(4));

		// ================ POST-SPINNER LABEL ================
		JLabel minutesLabel = new JLabel("Minute(s)");
		minutesLabel.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		frequentPanel.add(minutesLabel);

		// ================ ADD TO CONTENT ================
		contentPanel.add(frequentPanel);
		contentPanel.add(Box.createVerticalStrut(ROW_SPACING));

	}

	private void buildHourlyPanel() {

		// ================ PANEL ================
		hourlyPanel = new JPanel();
		hourlyPanel.setLayout(new BoxLayout(hourlyPanel, BoxLayout.X_AXIS));

		// ================ RADIO BUTTON ================
		hourlyButton = new JRadioButton("Hourly");
		hourlyButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		hourlyPanel.add(hourlyButton);

		hourlyPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(hourlyButton);

		// ================ PRE-SPINNER LABEL ================
		JLabel everyLabel = new JLabel("Every");
		everyLabel.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		hourlyPanel.add(everyLabel);

		hourlyPanel.add(Box.createHorizontalStrut(4));

		// ================ SPINNER + MODEL ================
		hourlyHoursSpinner = new JSpinner();
		hourlyHoursSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		hourlyHoursSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		hourlyHoursSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		hourlyHoursSpinner.setModel(new SpinnerNumberModel(1, 1, 23, 1));
		hourlyPanel.add(hourlyHoursSpinner);

		hourlyPanel.add(Box.createHorizontalStrut(4));

		// ================= POST-SPINNER LABEL ================
		JLabel hoursLabel = new JLabel("Hour(s)");
		hoursLabel.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		hourlyPanel.add(hoursLabel);

		// ================ ADD TO CONTENT ================
		contentPanel.add(hourlyPanel);
		contentPanel.add(Box.createVerticalStrut(ROW_SPACING));

	}

	private void buildDailyPanel() {

		// ================ PANEL ================
		dailyPanel = new JPanel();
		dailyPanel.setLayout(new BoxLayout(dailyPanel, BoxLayout.X_AXIS));

		// ================ RADIO BUTTON ================
		dailyButton = new JRadioButton("Daily");
		dailyButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyPanel.add(dailyButton);

		dailyPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(dailyButton);

		// ================ HOUR SPINNER + MODEL ================
		dailyHourSpinner = new JSpinner();
		dailyHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyHourSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		dailyHourSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		dailyHourSpinner.setModel(new SpinnerNumberModel(12, 1, 12, 1));
		dailyPanel.add(dailyHourSpinner);

		dailyPanel.add(Box.createHorizontalStrut(4));

		// ================ MINUTE SELECTOR + MODEL ================
		dailyMinuteSpinner = new JSpinner();
		dailyMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyMinuteSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		dailyMinuteSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		dailyMinuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		dailyMinuteSpinner.setEditor(new JSpinner.NumberEditor(dailyMinuteSpinner, "00"));

		dailyPanel.add(dailyMinuteSpinner);
		dailyPanel.add(Box.createHorizontalStrut(4));

		// ================ AM/PM SELECTOR ================
		dailyAmButton = new JRadioButton("AM");
		dailyAmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyPanel.add(dailyAmButton);

		dailyPmButton = new JRadioButton("PM");
		dailyPmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyPanel.add(dailyPmButton);

		// select AM by default
		dailyAmButton.setSelected(true);

		ButtonGroup amPmGroup = new ButtonGroup();
		amPmGroup.add(dailyAmButton);
		amPmGroup.add(dailyPmButton);

		// ================ ADD TO CONTENT ================
		contentPanel.add(dailyPanel);
		contentPanel.add(Box.createVerticalStrut(ROW_SPACING));
	}

	private void buildWeeklyPanel() {

		// ================ PANEL ================
		weeklyPanel = new JPanel();
		weeklyPanel.setLayout(new BoxLayout(weeklyPanel, BoxLayout.X_AXIS));

		// ================ RADIO BUTTON ===============
		weeklyButton = new JRadioButton("Weekly");
		weeklyButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyPanel.add(weeklyButton);

		weeklyPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(weeklyButton);

		// ================ WEEKDAY CHECK BOXES ================
		dayCheckboxes = new ArrayList<>();

		JCheckBox mondayBox = new JCheckBox("M");
		mondayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dayCheckboxes.add(mondayBox);
		weeklyPanel.add(mondayBox);

		JCheckBox tuesdayBox = new JCheckBox("T");
		tuesdayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dayCheckboxes.add(tuesdayBox);
		weeklyPanel.add(tuesdayBox);

		JCheckBox wednesdayBox = new JCheckBox("W");
		wednesdayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dayCheckboxes.add(wednesdayBox);
		weeklyPanel.add(wednesdayBox);

		JCheckBox thursdayBox = new JCheckBox("Th");
		thursdayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dayCheckboxes.add(thursdayBox);
		weeklyPanel.add(thursdayBox);

		JCheckBox fridayBox = new JCheckBox("F");
		fridayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dayCheckboxes.add(fridayBox);
		weeklyPanel.add(fridayBox);

		JCheckBox saturdayBox = new JCheckBox("Sa");
		saturdayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dayCheckboxes.add(saturdayBox);
		weeklyPanel.add(saturdayBox);

		JCheckBox sundayBox = new JCheckBox("Su");
		sundayBox.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dayCheckboxes.add(sundayBox);
		weeklyPanel.add(sundayBox);

		weeklyPanel.add(Box.createHorizontalStrut(4));

		// ================ HOUR SPINNER + MODEL ================
		weeklyHourSpinner = new JSpinner();
		weeklyHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyHourSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		weeklyHourSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		weeklyHourSpinner.setModel(new SpinnerNumberModel(12, 1, 12, 1));
		weeklyPanel.add(weeklyHourSpinner);

		// ================ MINUTE SPINNER + MODEL ================
		weeklyMinuteSpinner = new JSpinner();
		weeklyMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyMinuteSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		weeklyMinuteSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		weeklyMinuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		weeklyMinuteSpinner.setEditor(new JSpinner.NumberEditor(weeklyMinuteSpinner, "00"));

		weeklyPanel.add(weeklyMinuteSpinner);
		weeklyPanel.add(Box.createHorizontalStrut(4));

		// ================ AM/PM SELECTOR ================
		weeklyAmButton = new JRadioButton("AM");
		weeklyAmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyPanel.add(weeklyAmButton);

		weeklyPmButton = new JRadioButton("PM");
		weeklyPmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		weeklyPanel.add(weeklyPmButton);

		// select AM by default
		weeklyAmButton.setSelected(true);

		ButtonGroup amPmGroup = new ButtonGroup();
		amPmGroup.add(weeklyAmButton);
		amPmGroup.add(weeklyPmButton);

		// ================ ADD TO CONTENT ================
		contentPanel.add(weeklyPanel);
		contentPanel.add(Box.createVerticalStrut(ROW_SPACING));

	}

	private void buildCustomPanel() {

		// ================ PANEL ================
		customPanel = new JPanel();
		customPanel.setLayout(new BoxLayout(customPanel, BoxLayout.X_AXIS));

		// ================ RADIO BUTTON ================
		customButton = new JRadioButton("Custom");
		customButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		customPanel.add(customButton);

		customPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(customButton);

		// ================ CUSTOM FIELD ================
		customCronField = new JTextField(24);
		customCronField.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		customCronField.setPreferredSize(new Dimension(240, SPINNER_HEIGHT));
		customCronField.setMaximumSize(new Dimension(240, SPINNER_HEIGHT));
		customPanel.add(customCronField);

		// TODO validation and builder button

		// ================ ADD TO CONTENT ================
		contentPanel.add(customPanel);
	}

	private void updatePanels() {

		// Based on which radio button is selected, activate one row and disable all the other rows
		if (frequentButton.isSelected()) {
			ComponentUtils.setComponentsEnabledRecursively(frequentPanel, true);
			ComponentUtils.setComponentsEnabledRecursively(hourlyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(dailyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(weeklyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(customPanel, false);
		}
		else if (hourlyButton.isSelected()) {
			ComponentUtils.setComponentsEnabledRecursively(frequentPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(hourlyPanel, true);
			ComponentUtils.setComponentsEnabledRecursively(dailyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(weeklyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(customPanel, false);
		}
		else if (dailyButton.isSelected()) {
			ComponentUtils.setComponentsEnabledRecursively(frequentPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(hourlyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(dailyPanel, true);
			ComponentUtils.setComponentsEnabledRecursively(weeklyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(customPanel, false);
		}
		else if (weeklyButton.isSelected()) {
			ComponentUtils.setComponentsEnabledRecursively(frequentPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(hourlyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(dailyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(weeklyPanel, true);
			ComponentUtils.setComponentsEnabledRecursively(customPanel, false);
		}
		else if (customButton.isSelected()) {
			ComponentUtils.setComponentsEnabledRecursively(frequentPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(hourlyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(dailyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(weeklyPanel, false);
			ComponentUtils.setComponentsEnabledRecursively(customPanel, true);
		}

		// explicitly enable all radio buttons
		frequentButton.setEnabled(true);
		hourlyButton.setEnabled(true);
		dailyButton.setEnabled(true);
		weeklyButton.setEnabled(true);
		customButton.setEnabled(true);

	}

	public JSONObject getScheduleJSON() {

		// Build a JSON representation of the user's schedule based on what they input in the GUI fields
		JSONObject result = new JSONObject();

		if (frequentButton.isSelected()) {
			result.put("type", "interval");
			result.put("round", false);
			JSONObject interval = new JSONObject();
			interval.put("unit", "minutes");
			interval.put("value", freqMinuteSpinner.getValue());
			result.put("interval", interval);
		}
		else if (hourlyButton.isSelected()) {
			result.put("type", "interval");
			result.put("round", true);
			JSONObject interval = new JSONObject();
			interval.put("unit", "hours");
			interval.put("value", hourlyHoursSpinner.getValue());
			result.put("interval", interval);
		}
		else if (dailyButton.isSelected()) {
			result.put("type", "daily");
			result.put("hour", dailyHourSpinner.getValue());
			result.put("minute", dailyMinuteSpinner.getValue());
			result.put("ampm", dailyAmButton.isSelected() ? "am" : dailyPmButton.isSelected() ? "pm" : "");
		}
		else if (weeklyButton.isSelected()) {
			result.put("type", "weekly");
			result.put("hour", weeklyHourSpinner.getValue());
			result.put("minute", weeklyMinuteSpinner.getValue());
			result.put("ampm", weeklyAmButton.isSelected() ? "am" : weeklyPmButton.isSelected() ? "pm" : "");
			JSONArray days = new JSONArray();
			for (JCheckBox dayCheckbox : dayCheckboxes) {
				if (dayCheckbox.isSelected())
					days.put(dayCheckbox.getText().toLowerCase());
			}
			result.put("days", days);
		}
		else if (customButton.isSelected()) {
			result.put("type", "cron");
			result.put("cron", customCronField.getText());
			// TODO verify cron string
		}

		return result;

	}

}
