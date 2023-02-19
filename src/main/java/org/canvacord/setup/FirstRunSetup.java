package org.canvacord.setup;

public class FirstRunSetup {

	/**
	 * Run the initial setup process.
	 * @return {@code true} if the user completes the setup process successfully.
	 */
	public static boolean runFirstTimeSetup() {

		TokenSetupWizard tokenSetupWizard = new TokenSetupWizard();
		tokenSetupWizard.runWizard();

		return tokenSetupWizard.completedSuccessfully();

	}

}
