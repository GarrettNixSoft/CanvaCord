package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.wizard.CanvaCordWizard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class InstanceCanvasFetchCard extends InstanceConfigCard {

	private JPanel hourlyPanel;
	private JPanel dailyPanel;
	private JPanel weeklyPanel;
	private JPanel customPanel;

	private ButtonGroup radioButtonGroup;
	private JRadioButton hourlyButton;
	private JRadioButton dailyButton;
	private JRadioButton weeklyButton;
	private JRadioButton customButton;

	private static final int CONTENT_PADDING_HORIZONTAL = 50;
	private static final int CONTENT_PADDING_VERTICAL = 24;
	private static final int HEADER_SPACING = 36;
	private static final int ROW_SPACING = 20;

	private static final int SPINNER_WIDTH = 40;

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

		ActionListener typeSelectionListener = event -> {
			updatePanels();
		};

		hourlyButton.addActionListener(typeSelectionListener);
		dailyButton.addActionListener(typeSelectionListener);
		weeklyButton.addActionListener(typeSelectionListener);
		customButton.addActionListener(typeSelectionListener);

	}

	private void buildHourlyPanel() {

		// ================ PANEL ================
		hourlyPanel = new JPanel();
		hourlyPanel.setLayout(new BoxLayout(hourlyPanel, BoxLayout.X_AXIS));

		// ================ RADIO BUTTON ================
		hourlyButton = new JRadioButton("Hourly");
		hourlyButton.setFont(CanvaCordFonts.LABEL_FONT);
		hourlyPanel.add(hourlyButton);

		hourlyPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(hourlyButton);

		// ================ SELECT HOURLY BY DEFAULT ================
		hourlyButton.setSelected(true);

		// ================ PRE-SPINNER LABEL ================
		JLabel everyLabel = new JLabel("Every");
		everyLabel.setFont(CanvaCordFonts.LABEL_FONT);
		hourlyPanel.add(everyLabel);

		hourlyPanel.add(Box.createHorizontalStrut(4));

		// ================ SPINNER + MODEL ================
		JSpinner hoursSpinner = new JSpinner();
		hoursSpinner.setFont(CanvaCordFonts.LABEL_FONT);
		hoursSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, 24));
		hoursSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, 24));
		hoursSpinner.setModel(new SpinnerNumberModel(1, 1, 23, 1));
		hourlyPanel.add(hoursSpinner);

		hourlyPanel.add(Box.createHorizontalStrut(4));

		// ================= POST-SPINNER LABEL ================
		JLabel hoursLabel = new JLabel("Hours");
		hoursLabel.setFont(CanvaCordFonts.LABEL_FONT);
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
		dailyButton.setFont(CanvaCordFonts.LABEL_FONT);
		dailyPanel.add(dailyButton);

		dailyPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(dailyButton);

		// ================ HOUR SPINNER + MODEL ================
		JSpinner hourSpinner = new JSpinner();
		hourSpinner.setFont(CanvaCordFonts.LABEL_FONT);
		hourSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, 24));
		hourSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, 24));
		hourSpinner.setModel(new SpinnerNumberModel(12, 1, 12, 1));
		dailyPanel.add(hourSpinner);

		dailyPanel.add(Box.createHorizontalStrut(4));

		// ================ MINUTE SELECTOR + MODEL ================
		JSpinner minuteSpinner = new JSpinner();
		minuteSpinner.setFont(CanvaCordFonts.LABEL_FONT);
		minuteSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, 24));
		minuteSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, 24));
		minuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		minuteSpinner.setEditor(new JSpinner.NumberEditor(minuteSpinner, "00"));

		dailyPanel.add(minuteSpinner);
		dailyPanel.add(Box.createHorizontalStrut(4));

		// ================ AM/PM SELECTOR ================
		JRadioButton amButton = new JRadioButton("AM");
		amButton.setFont(CanvaCordFonts.LABEL_FONT);
		dailyPanel.add(amButton);

		JRadioButton pmButton = new JRadioButton("PM");
		pmButton.setFont(CanvaCordFonts.LABEL_FONT);
		dailyPanel.add(pmButton);

		// select AM by default
		amButton.setSelected(true);

		ButtonGroup amPmGroup = new ButtonGroup();
		amPmGroup.add(amButton);
		amPmGroup.add(pmButton);

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
		weeklyButton.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(weeklyButton);

		weeklyPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(weeklyButton);

		// ================ WEEKDAY CHECK BOXES ================
		JCheckBox mondayBox = new JCheckBox("M");
		mondayBox.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(mondayBox);

		JCheckBox tuesdayBox = new JCheckBox("T");
		tuesdayBox.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(tuesdayBox);

		JCheckBox wednesdayBox = new JCheckBox("W");
		wednesdayBox.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(wednesdayBox);

		JCheckBox thursdayBox = new JCheckBox("Th");
		thursdayBox.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(thursdayBox);

		JCheckBox fridayBox = new JCheckBox("F");
		fridayBox.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(fridayBox);

		JCheckBox saturdayBox = new JCheckBox("Sa");
		saturdayBox.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(saturdayBox);

		JCheckBox sundayBox = new JCheckBox("Su");
		sundayBox.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(sundayBox);

		weeklyPanel.add(Box.createHorizontalStrut(4));

		// ================ HOUR SPINNER + MODEL ================
		JSpinner hourSpinner = new JSpinner();
		hourSpinner.setFont(CanvaCordFonts.LABEL_FONT);
		hourSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, 24));
		hourSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, 24));
		hourSpinner.setModel(new SpinnerNumberModel(12, 1, 12, 1));
		weeklyPanel.add(hourSpinner);

		// ================ MINUTE SPINNER + MODEL ================
		JSpinner minuteSpinner = new JSpinner();
		minuteSpinner.setFont(CanvaCordFonts.LABEL_FONT);
		minuteSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, 24));
		minuteSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, 24));
		minuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		minuteSpinner.setEditor(new JSpinner.NumberEditor(minuteSpinner, "00"));

		weeklyPanel.add(minuteSpinner);
		weeklyPanel.add(Box.createHorizontalStrut(4));

		// ================ AM/PM SELECTOR ================
		JRadioButton amButton = new JRadioButton("AM");
		amButton.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(amButton);

		JRadioButton pmButton = new JRadioButton("PM");
		pmButton.setFont(CanvaCordFonts.LABEL_FONT);
		weeklyPanel.add(pmButton);

		// select AM by default
		amButton.setSelected(true);

		ButtonGroup amPmGroup = new ButtonGroup();
		amPmGroup.add(amButton);
		amPmGroup.add(pmButton);

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
		customButton.setFont(CanvaCordFonts.LABEL_FONT);
		customPanel.add(customButton);

		customPanel.add(Box.createHorizontalGlue());

		// ================ ADD BUTTON TO GROUP ================
		radioButtonGroup.add(customButton);

		// ================ CUSTOM FIELD ================
		JTextField customCronField = new JTextField(24);
		customCronField.setFont(CanvaCordFonts.LABEL_FONT);
		customCronField.setPreferredSize(new Dimension(240, 24));
		customCronField.setMaximumSize(new Dimension(240, 24));
		customPanel.add(customCronField);

		// TODO validation and builder button

		// ================ ADD TO CONTENT ================
		contentPanel.add(customPanel);
	}

	private void updatePanels() {

		if (hourlyButton.isSelected()) {
			setComponentsEnabledRecursively(hourlyPanel, true);
			setComponentsEnabledRecursively(dailyPanel, false);
			setComponentsEnabledRecursively(weeklyPanel, false);
			setComponentsEnabledRecursively(customPanel, false);
		}
		else if (dailyButton.isSelected()) {
			setComponentsEnabledRecursively(hourlyPanel, false);
			setComponentsEnabledRecursively(dailyPanel, true);
			setComponentsEnabledRecursively(weeklyPanel, false);
			setComponentsEnabledRecursively(customPanel, false);
		}
		else if (weeklyButton.isSelected()) {
			setComponentsEnabledRecursively(hourlyPanel, false);
			setComponentsEnabledRecursively(dailyPanel, false);
			setComponentsEnabledRecursively(weeklyPanel, true);
			setComponentsEnabledRecursively(customPanel, false);
		}
		else if (customButton.isSelected()) {
			setComponentsEnabledRecursively(hourlyPanel, false);
			setComponentsEnabledRecursively(dailyPanel, false);
			setComponentsEnabledRecursively(weeklyPanel, false);
			setComponentsEnabledRecursively(customPanel, true);
		}

		// explicitly enable all radio buttons
		hourlyButton.setEnabled(true);
		dailyButton.setEnabled(true);
		weeklyButton.setEnabled(true);
		customButton.setEnabled(true);

	}

	private void setComponentsEnabledRecursively(JComponent component, boolean enabled) {

		// set the flag for the target component
		component.setEnabled(enabled);

		// set the flag recursively for all subcomponents
		for (Component subcomponent : component.getComponents()) {
			if (subcomponent instanceof JComponent jComponent)
				setComponentsEnabledRecursively(jComponent, enabled);
		}

	}

}
