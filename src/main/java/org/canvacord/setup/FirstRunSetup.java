package org.canvacord.setup;

import org.canvacord.util.input.UserInput;

public class FirstRunSetup {

	/**
	 * Run the initial setup process.
	 * @return {@code true} if the user completes the setup process successfully.
	 */
	public static boolean runFirstTimeSetup() {

		TokenSetupWizard tokenSetupWizard = new TokenSetupWizard();
		tokenSetupWizard.runWizard();

		boolean success = tokenSetupWizard.completedSuccessfully();

		if (success) {
			UserInput.showMessage("You have successfully configured your\nAPI tokens. Do not share them with\nanyone; they are equivalent to your\nusername and password.", "Tokens Added");
		}

		return success;

	}

}
