package org.canvacord;

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

//		InstanceCreateWizard wizard = new InstanceCreateWizard();
//		wizard.runWizard();
//
//		if (wizard.completedSuccessfully()) {
//			System.out.println("Success!");
//		}
//		else {
//			if (wizard.isCancelled())
//				System.err.printf("Cancelled.");
//			else
//				System.err.println("Failed.");
//		}

		InstanceManager.generateNewInstance().ifPresentOrElse(
				instanceID -> {
					System.out.println("Successfully generated instance " + instanceID);
				},
				() -> {
					System.out.println("Failed instance creation, for one reason or another");
				}
		);

	}

}
