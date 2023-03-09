package org.canvacord;

import org.canvacord.discord.DiscordBot;
import org.canvacord.instance.InstanceManager;
import org.canvacord.setup.InstanceCreateWizard;

import javax.swing.*;

public class InstanceSetupTest {

	static {
		// make the UI look native to the user's platform
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {}
	}

	public static void main(String[] args) {

		InstanceManager.generateNewInstance().ifPresentOrElse(
				instance -> System.out.println("Successfully generated instance " + instance.getName()),
				() -> System.out.println("Failed instance creation, for one reason or another")
		);

		DiscordBot.getBotInstance().disconnect();

	}

}
