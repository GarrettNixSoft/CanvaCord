package org.canvacord;

import org.canvacord.setup.TokenSetupWizard;

import javax.swing.*;

public class TokenSetupTest {

	static {
		// make the UI look native to the user's platform
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {}
	}

	public static void main(String[] args) {

		TokenSetupWizard tokenSetupWizard = new TokenSetupWizard();
		if (tokenSetupWizard.runWizard()) {
			System.out.println("Success!");
		} else {
			if (tokenSetupWizard.isCancelled())
				System.err.println("Cancelled.");
			else
				System.err.println("Failed.");
		}

	}

}
