package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.instance.Instance;

import javax.swing.*;

public class MeetingMarkersPage extends OptionPage {

	private JCheckBox doMeetingMarkers;
	private JCheckBox createMarkersRole;

	public MeetingMarkersPage() {
		super("Meeting Markers");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("", "[][][][][]", "[][][]"));

		JLabel meetingMarkersLabel = new JLabel("Meeting Markers:");
		meetingMarkersLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(meetingMarkersLabel, "cell 0 0");

		doMeetingMarkers = new JCheckBox("Send Meeting Markers");
		doMeetingMarkers.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(doMeetingMarkers, "cell 0 2");

		createMarkersRole = new JCheckBox("Create Markers Role");
		createMarkersRole.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(createMarkersRole, "cell 0 4");

	}

	@Override
	protected void initLogic() {

		doMeetingMarkers.addActionListener(event -> {
			createMarkersRole.setEnabled(doMeetingMarkers.isSelected());
		});

	}

	@Override
	protected void prefillGUI() {

		doMeetingMarkers.setSelected((Boolean) dataStore.get("do_meeting_markers"));
		createMarkersRole.setSelected((Boolean) dataStore.get("create_markers_role"));

		// only enable the second checkbox if the first is selected
		createMarkersRole.setEnabled(doMeetingMarkers.isSelected());

	}

	@Override
	protected void verifyInputs() throws Exception {

		// No errors possible here
		dataStore.store("do_meeting_markers", doMeetingMarkers.isSelected());
		dataStore.store("create_markers_role", createMarkersRole.isSelected());

	}
}
