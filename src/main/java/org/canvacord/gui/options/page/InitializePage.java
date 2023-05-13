package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.instance.Instance;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.time.LongTaskDialog;

import javax.swing.*;

public class InitializePage extends OptionPage {

	private JButton initializeButton;
	private JButton verifyButton;

	public InitializePage() {
		super("Initialize");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("", "[][]", "[][][][]"));

		initializeButton = new JButton("Initialize");
		initializeButton.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(initializeButton, "cell 1 1");

		verifyButton = new JButton("Verify");
		verifyButton.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(verifyButton, "cell 1 3");

		// TODO
	}

	@Override
	protected void initLogic() {

		initializeButton.addActionListener(event -> {
			Instance instance = (Instance) dataStore.get("instance");
			LongTaskDialog.runLongTask(
					() -> {
						boolean success = instance.initialize();
						if (success) {
							UserInput.showMessage("Instance initialized.", "Success");
							initializeButton.setEnabled(false);
							initializeButton.setToolTipText("This instance is already initialized.");

						}
						else UserInput.showErrorMessage("Instance initialization failed. Check\nthe logs for more information.", "Failed");
					},
					"Initializing " + instance.getName(),
					"Initialize Instance"
			);
		});

		verifyButton.addActionListener(event -> {
			Instance instance = (Instance) dataStore.get("instance");
			LongTaskDialog.runLongTask(
					() -> {
						boolean success = instance.verify();
						if (success) UserInput.showMessage("Instance verified.", "Success");
						else UserInput.showErrorMessage("Instance verification failed. Check\nthe logs for more information.", "Failed");
					},
					"Verifying " + instance.getName(),
					"Verify Instance"
			);
		});

	}

	@Override
	protected void prefillGUI() {
		boolean verified = (Boolean) dataStore.get("initialized");
		if (verified) {
			initializeButton.setEnabled(false);
			initializeButton.setToolTipText("This instance is already initialized.");
		}
		else {
			initializeButton.setEnabled(true);
			initializeButton.setToolTipText(null);
		}
	}

	@Override
	protected void verifyInputs() throws Exception {
		// TODO
	}
}
