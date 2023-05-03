package org.canvacord.gui.dialog;

import org.canvacord.entity.ClassMeeting;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorIcon;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.resources.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.util.List;
import java.util.*;

public class ClassScheduleDialog extends CanvaCordDialog {

	private static final int WIDTH = 480;
	private static final int HEIGHT = 400;

	private List<ClassMeeting> classMeetings;
	private JList<ClassMeeting> classMeetingList;

	private JButton newMeetingButton;
	private JButton editMeetingButton;
	private JButton deleteMeetingButton;

	public ClassScheduleDialog() {
		super("Class Schedule Builder", WIDTH, HEIGHT);
		buildGUI();
		initLogic();
	}

	@Override
	protected boolean verifyInputs() {
		if (classMeetings.isEmpty()) {
			UserInput.showErrorMessage("You must add at least one meeting.", "Empty Schedule");
			return false;
		}
		return true;
	}

	private void buildGUI() {

		// prepare collections
		classMeetings = new ArrayList<>();

		// positioning
		final int componentX = 20;

		final int buttonSize = 48;
		final int buttonSpacing = 10;
		final int buttonX = 392;
		final int buttonY = 120;

		// Label the dialog
		JLabel dialogLabel = new JLabel(
				"""
					<html>Use the buttons on the right to add, edit, or remove class meetings.
					A class meeting represents one session of your class per week. If your class
					meets more than once per week, you can add multiple class meeting entries.
					</html>"""
		);
		dialogLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		dialogLabel.setBounds(componentX, 4, WIDTH - 60, 60);
		add(dialogLabel);

		// Label the list
		JLabel listLabel = new JLabel("Class Meetings:");
		listLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		listLabel.setBounds(componentX, 80, 120, 28);
		add(listLabel);

		// Show meetings in a scrollable list
		JScrollPane classMeetingsPane = new JScrollPane();
		classMeetingsPane.setBounds(componentX, 110, WIDTH - 130, HEIGHT - 210);
		add(classMeetingsPane);

		classMeetingList = new JList<>();
		classMeetingsPane.getViewport().setView(classMeetingList);

		// create a new meeting
		newMeetingButton = new JButton(ImageLoader.loadIcon("new_icon.png"));
		newMeetingButton.setBounds(buttonX, buttonY, buttonSize, buttonSize);
		add(newMeetingButton);

		// edit a meeting
		editMeetingButton = new JButton(ImageLoader.loadIcon("edit_icon_wip.png"));
		editMeetingButton.setBounds(buttonX, buttonY + buttonSize + buttonSpacing, buttonSize, buttonSize);
		add(editMeetingButton);

		// delete a meeting
		deleteMeetingButton = new JButton(ImageLoader.loadIcon("delete_icon_non_beveled.png"));
		deleteMeetingButton.setBounds(buttonX, buttonY + (buttonSize + buttonSpacing) * 2, buttonSize, buttonSize);
		add(deleteMeetingButton);

		// show meetings list
		classMeetingList.setFixedCellHeight(-1);
		classMeetingList.setLayoutOrientation(JList.VERTICAL);
		classMeetingList.setCellRenderer(new ClassScheduleDialog.MeetingCellRenderer(classMeetingList));

	}

	private void initLogic() {

		// ================ NEW MEETING ================
		newMeetingButton.addActionListener(event -> {
			ClassMeetingDialog.buildClassMeeting().ifPresent(
					meeting -> {
						// TODO check for overlap conflicts
						classMeetings.add(meeting);
						updateMeetingsList();
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
						classMeetings.set(index, editedMeeting);
						updateMeetingsList();
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
				classMeetings.remove(index);
				updateMeetingsList();
			}
		});

	}

	private void updateMeetingsList() {

		ListModel<ClassMeeting> meetingListModel = new AbstractListModel<>() {
			@Override
			public int getSize() {
				return classMeetings.size();
			}

			@Override
			public ClassMeeting getElementAt(int index) {
				return classMeetings.get(index);
			}
		};

		classMeetingList.setModel(meetingListModel);
		classMeetingList.updateUI();

	}

	private static class MeetingCellRenderer extends JLabel implements ListCellRenderer<ClassMeeting> {

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

		public MeetingCellRenderer(JList parent) {
			setPreferredSize(new Dimension(parent.getWidth(), SIZE));
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(SIZE, SIZE);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends ClassMeeting> list, ClassMeeting meeting, int index, boolean isSelected, boolean cellHasFocus) {

			// Show day and times
			setText(String.format("<html><b>%s</b><br/>%s</html>", meeting.getWeekdayStr(), meeting.getTimeDescription()));

			// Generate a color icon
			ColorIcon colorIcon = new ColorIcon(dayOfWeekColorMap.get(meeting.getWeekday()), SIZE, SIZE);
			colorIcon.setDoBorder(true);
			setIcon(colorIcon);

			return this;

		}
	}

	public List<ClassMeeting> getResult() {
		if (cancelled || !verifyInputs())
			return new ArrayList<>();
		else {
			return Collections.unmodifiableList(classMeetings);
		}
	}

	public static List<ClassMeeting> buildClassSchedule() {
		ClassScheduleDialog dialog = new ClassScheduleDialog();
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getResult();
	}

	// TODO edit mode

}
