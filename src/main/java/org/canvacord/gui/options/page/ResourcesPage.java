package org.canvacord.gui.options.page;

public class ResourcesPage extends InfoPage {

	public ResourcesPage() {
		super("Resources");
	}

	@Override
	protected String getInfoText() {
		return """
				<html>
				Add or remove course resources. You can add one file for the course syllabus,
				which will be accessible to users in your Discord server via the <b>/syllabus</b>
				command (provided you have it enabled). Additionally, you can add as many textbook
				files as you need in PDF form, which can be accessed by users using the <b>/textbook</b>
				command and its derivatives (again, provided you enable it).
				</html>
				""";
	}
}
