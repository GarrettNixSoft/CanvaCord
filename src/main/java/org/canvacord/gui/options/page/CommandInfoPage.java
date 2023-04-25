package org.canvacord.gui.options.page;

public class CommandInfoPage extends InfoPage {

	public CommandInfoPage() {
		super("Commands");
	}

	@Override
	protected String getInfoText() {
		return """
				<html>
				CanvaCord offers several commands users can invoke from your Discord server to
				access Canvas information and other resources. You can choose which commands to
				enable or disable. For some commands, you can enable or disable certain subcommands
				or features to control how the command can be used.
				</html>
				""";
	}
}
