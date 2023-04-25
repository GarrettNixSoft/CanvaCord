package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.entity.CanvasFetchScheduleType;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.util.CanvaCordModels;
import org.canvacord.util.time.CanvaCordTime;

import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.canvacord.entity.CanvasFetchScheduleType.*;

public class CanvasFetchPage extends OptionPage {

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
			scheduleTypeSelector.addItem(type);
			componentLists.put(type, new ArrayList<>());
		}

		// ================ FREQUENT COMPONENTS ================
		JLabel everyLabel = new JLabel("Every:");
		everyLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		componentLists.get(FREQUENT).add(everyLabel);
		layoutStrings.put(everyLabel, "cell 0 2");

		frequentValueSpinner = new JSpinner();
		frequentValueSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		frequentValueSpinner.setModel(CanvaCordModels.getMinutesModel());
		componentLists.get(FREQUENT).add(frequentValueSpinner);
		layoutStrings.put(frequentValueSpinner, "cell 1 2");

		JLabel minutesLabel = new JLabel("Minute(s)");
		minutesLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		componentLists.get(FREQUENT).add(minutesLabel);
		layoutStrings.put(minutesLabel, "cell 2 2");

		// ================ HOURLY COMPONENTS ================
		componentLists.get(HOURLY).add(everyLabel);

		hourlyValueSpinner = new JSpinner();
		hourlyValueSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		hourlyValueSpinner.setModel(CanvaCordModels.getHoursModel());
		componentLists.get(HOURLY).add(hourlyValueSpinner);
		layoutStrings.put(hourlyValueSpinner, "cell 1 2");

		JLabel hoursLabel = new JLabel("Hour(s)");
		hoursLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		componentLists.get(HOURLY).add(hoursLabel);
		layoutStrings.put(hoursLabel, "cell 2 2");

		// ================ DAILY COMPONENTS ================
		JLabel preLabel = new JLabel("Every day at:");
		preLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		componentLists.get(DAILY).add(preLabel);
		layoutStrings.put(preLabel, "cell 0 2");

		dailyMinuteSpinner = new JSpinner();
		dailyMinuteSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyMinuteSpinner.setModel(CanvaCordModels.getMinutesModel());
		dailyMinuteSpinner.setEditor(CanvaCordModels.getMinutesEditor(dailyMinuteSpinner));
		componentLists.get(DAILY).add(dailyMinuteSpinner);
		layoutStrings.put(dailyMinuteSpinner, "cell 2 2");

		dailyHourSpinner = new JSpinner();
		dailyHourSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		dailyHourSpinner.setModel(CanvaCordModels.getHoursModel());
		componentLists.get(DAILY).add(dailyHourSpinner);
		layoutStrings.put(dailyHourSpinner, "cell 1 2");

		dailyAmButton = new JRadioButton("AM");
		dailyAmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		componentLists.get(DAILY).add(dailyAmButton);
		layoutStrings.put(dailyAmButton, "cell 3 2");

		dailyPmButton = new JRadioButton("PM");
		dailyPmButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		componentLists.get(DAILY).add(dailyPmButton);
		layoutStrings.put(dailyPmButton, "cell 4 2");

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(dailyAmButton);
		buttonGroup.add(dailyPmButton);

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
		}

		componentLists.get(WEEKLY).add(weekdaysPanel);
		layoutStrings.put(weekdaysPanel, "cell 0 2");

		// ================ CRON COMPONENTS ================

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
		// TODO
	}

	@Override
	protected void verifyInputs() throws CanvaCordException {
		// TODO
	}

	private void updateScheduleGUI(String newScheduleType) {
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
}
