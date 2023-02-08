package org.canvacord;

import org.canvacord.setup.InstanceCreateWizard;

import javax.swing.*;

public class InstanceSetupTest {

	static {
		// make the UI look native to the user's platform
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {}
	}

	public static void main(String[] args) {

		InstanceCreateWizard wizard = new InstanceCreateWizard();
		wizard.runWizard();

		if (wizard.completedSuccessfully()) {
			System.out.println("Success!");
		}
		else {
			if (wizard.isCancelled())
				System.err.printf("Cancelled.");
			else
				System.err.println("Failed.");
		}

	}

}
