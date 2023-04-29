package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.entity.ClassMeeting;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorIcon;
import org.canvacord.gui.dialog.ClassMeetingDialog;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSchedulePage extends OptionPage {

	private List<ClassMeeting> classSchedule;
	private JList<ClassMeeting> classMeetingList;

	private JButton newMeetingButton;
	private JButton editMeetingButton;
	private JButton deleteMeetingButton;

	public CourseSchedulePage() {
		super("Course Schedule");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("insets 10 10 10 10", "[grow][]", "[]".repeat(20)));

		JLabel scheduleLabel = new JLabel("Class Schedule:");
		scheduleLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(scheduleLabel, "cell 0 0");

		classMeetingList = new JList<>();
		classMeetingList.setFixedCellHeight(-1);
		classMeetingList.setLayoutOrientation(JList.VERTICAL);
		classMeetingList.setCellRenderer(new ClassMeetingCellRenderer());

		JScrollPane meetingPane = new JScrollPane();
		meetingPane.getViewport().setView(classMeetingList);
		add(meetingPane, "cell 0 1 5 10, growx, growy");

		newMeetingButton = new JButton(new ImageIcon("resources/new_icon.png"));
		add(newMeetingButton, "cell 9 3");

		editMeetingButton = new JButton(new ImageIcon("resources/edit_icon_wip.png"));
		add(editMeetingButton, "cell 9 6");

		deleteMeetingButton = new JButton(new ImageIcon("resources/delete_icon_non_beveled.png"));
		add(deleteMeetingButton, "cell 9 9");

		// TODO
	}

	@Override
	protected void initLogic() {

		// ================ NEW MEETING ================
		newMeetingButton.addActionListener(event -> {
			ClassMeetingDialog.buildClassMeeting().ifPresent(
					meeting -> {
						// TODO check for overlap conflicts
						classSchedule.add(meeting);
						updateMeetingList();
					}
			);
		});

		// ================ EDIT MEETING ================
		editMeetingButton.addActionListener(event -> {
			// Get selected meeting
			ClassMeeting classMeeting = classMeetingList.getSelectedValue();
			if (classMeeting == null) return;
			int index = classMeetingList.getSelectedIndex();
			ClassMeetingDialog.editClassMeeting(classMeeting).ifPresent(
					editedMeeting -> {
						classSchedule.set(index, editedMeeting);
						updateMeetingList();
					}
			);
		});

		// ================ DELETE MEETING ================
		deleteMeetingButton.addActionListener(event -> {
			// Get selected meeting
			ClassMeeting classMeeting = classMeetingList.getSelectedValue();
			if (classMeeting == null) return;
			int index = classMeetingList.getSelectedIndex();
			// Ask for confirmation and delete if given
			if (UserInput.askToConfirm("Delete this meeting?", "Confirm Deletion")) {
				classSchedule.remove(index);
				updateMeetingList();
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void prefillGUI() {
		classSchedule = (List<ClassMeeting>) dataStore.get("class_schedule");
		updateMeetingList();
	}

	@Override
	protected void verifyInputs() throws Exception {
		// TODO
	}

	private static class ClassMeetingCellRenderer extends JLabel implements ListCellRenderer<ClassMeeting> {

		private static final Map<DayOfWeek, Color> dayOfWeekColorMap = new HashMap<>();

		static {
			dayOfWeekColorMap.put(DayOfWeek.MONDAY, Color.RED);
			dayOfWeekColorMap.put(DayOfWeek.TUESDAY, Color.ORANGE);
			dayOfWeekColorMap.put(DayOfWeek.WEDNESDAY, Color.YELLOW);
			dayOfWeekColorMap.put(DayOfWeek.THURSDAY, Color.GREEN);
			dayOfWeekColorMap.put(DayOfWeek.FRIDAY, Color.CYAN);
			dayOfWeekColorMap.put(DayOfWeek.SATURDAY, Color.BLUE);
			dayOfWeekColorMap.put(DayOfWeek.SUNDAY, Color.MAGENTA);
		}

		private static final int SIZE = 50;

		@Override
		public Component getListCellRendererComponent(JList<? extends ClassMeeting> list, ClassMeeting meeting, int index, boolean isSelected, boolean cellHasFocus) {

			// Show day and times
			setText(String.format("<html><b>%s</b><br/>%s</html>", meeting.getWeekdayStr(), meeting.getTimeDescription()));

			// Generate a color icon
			ColorIcon colorIcon = new ColorIcon(dayOfWeekColorMap.get(meeting.getWeekday()), SIZE, SIZE);
			colorIcon.setDoBorder(true);
			setIcon(colorIcon);

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			setOpaque(true);

			return this;

		}
	}

	private void updateMeetingList() {

		ListModel<ClassMeeting> meetingListModel = new AbstractListModel<>() {
			@Override
			public int getSize() {
				return classSchedule.size();
			}

			@Override
			public ClassMeeting getElementAt(int index) {
				return classSchedule.get(index);
			}
		};

		classMeetingList.setModel(meetingListModel);
		classMeetingList.updateUI();

	}

}
