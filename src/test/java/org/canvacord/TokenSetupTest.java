package org.canvacord;

import org.canvacord.setup.TokenSetupWizard;

public class TokenSetupTest {

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
