package org.canvacord.gui.options.page;

public class MeetingsPage extends InfoPage {

	public MeetingsPage() {
		super("Meetings");
	}

	@Override
	protected String getInfoText() {
		return """
				<html>
				You can configure your course's meeting schedule to enable <b>Meeting Reminders</b> and
				<b>Meeting Markers</b>. If you have provided a Syllabus file, CanvaCord may be able to
				scan it to automatically detect your course schedule. Otherwise, you can configure it
				manually.
				</html>
				""";
	}
}
