package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.instance.Instance;
import org.canvacord.util.CanvaCordModels;
import org.canvacord.util.gui.ComponentUtils;

import javax.swing.*;
import java.util.List;

public class MeetingRemindersPage extends OptionPage {

	private JCheckBox doMeetingReminders;
	private JCheckBox createRemindersRole;
	private JSpinner reminderScheduleSpinner;

	public MeetingRemindersPage() {
		super("Meeting Reminders");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("", "[grow]", "[][][][][][][]"));

		JLabel remindersLabel = new JLabel("Meeting Reminders:");
		remindersLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(remindersLabel, "cell 0 0");

		doMeetingReminders = new JCheckBox("Send Meeting Reminders");
		doMeetingReminders.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(doMeetingReminders, "cell 0 2");

		createRemindersRole = new JCheckBox("Create Reminders Role");
		createRemindersRole.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(createRemindersRole, "cell 0 4");

		JPanel schedulePanel = new JPanel();
		schedulePanel.setLayout(new MigLayout("", "[grow][][grow]", "[]"));
		add(schedulePanel, "cell 0 6");

		JLabel preScheduleLabel = new JLabel("Send reminders: ");
		preScheduleLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		schedulePanel.add(preScheduleLabel, "cell 0 0");

		reminderScheduleSpinner = new JSpinner();
		reminderScheduleSpinner.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		reminderScheduleSpinner.setModel(CanvaCordModels.getGenericNumberModel(5));
		schedulePanel.add(reminderScheduleSpinner, "cell 1 0, width 40px!");

		JLabel postScheduleLabel = new JLabel(" minutes before each meeting");
		postScheduleLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		schedulePanel.add(postScheduleLabel, "cell 2 0");


		setOnNavigateTo(() -> {
			if (((List<ClassMeeting>) dataStore.get("class_schedule")).isEmpty()) {
				ComponentUtils.setComponentsEnabledRecursively(this, false);
				doMeetingReminders.setSelected(false);
				createRemindersRole.setSelected(false);
				doMeetingReminders.setToolTipText("<html>You must configure a class schedule to<br/>enable meeting reminders.</html>");
				createRemindersRole.setToolTipText("<html>You must configure a class schedule to<br/>enable meeting reminders.</html>");
				reminderScheduleSpinner.setToolTipText("<html>You must configure a class schedule to<br/>enable meeting reminders.</html>");
			}
			else {
				ComponentUtils.setComponentsEnabledRecursively(this, true);
				doMeetingReminders.setToolTipText(null);
				createRemindersRole.setToolTipText(null);
				reminderScheduleSpinner.setToolTipText(null);
			}
		});

	}

	@Override
	protected void initLogic() {
		// TODO
	}

	@Override
	protected void prefillGUI() {
		doMeetingReminders.setSelected((Boolean) dataStore.get("do_meeting_reminders"));
		createRemindersRole.setSelected((Boolean) dataStore.get("create_reminders_role"));
		int reminderSchedule = (Integer) dataStore.get("class_reminder_schedule");
		reminderScheduleSpinner.setValue(reminderSchedule);
	}

	@Override
	protected void verifyInputs() throws Exception {
		// TODO
	}
}
