package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceCleanUp;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import java.awt.*;

public class CleanUpPage extends OptionPage {

	private JCheckBox doCleanUp;
	private JRadioButton[] cleanUpOptions;
	private JButton cleanUpNow;

	public CleanUpPage() {
		super("Clean Up");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("", "[][]", "[][][][][][]"));

		JLabel pageLabel = new JLabel("End-of-Semester Clean-Up");
		pageLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(pageLabel, "cell 0 0");

		doCleanUp = new JCheckBox("Clean up my data when the semester ends");
		doCleanUp.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(doCleanUp, "cell 0 2");

		JLabel optionsLabel = new JLabel("Clean Up Action:");
		optionsLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(optionsLabel, "cell 0 4");

		cleanUpOptions = new JRadioButton[2];
		ButtonGroup buttonGroup = new ButtonGroup();

		cleanUpOptions[0] = new JRadioButton("Archive");
		cleanUpOptions[0].setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add(cleanUpOptions[0], "cell 1 4");

		cleanUpOptions[1] = new JRadioButton("Delete");
		cleanUpOptions[1].setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		add((cleanUpOptions[1]), "cell 1 6");

		buttonGroup.add(cleanUpOptions[0]);
		buttonGroup.add(cleanUpOptions[1]);

		cleanUpNow = new JButton("Clean Up Now");
		cleanUpNow.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		cleanUpNow.setForeground(Color.RED);
		add(cleanUpNow, "cell 0 8");

		// Preselect default
		cleanUpOptions[0].setSelected(true);

	}

	@Override
	protected void initLogic() {

		cleanUpNow.addActionListener(event -> {

			if (UserInput.askToConfirm("Are you sure you want to run clean up\non this Instance? It may not be recoverable.", "Confirm Clean-Up")) {
				Instance instance = (Instance) dataStore.get("instance");
				InstanceCleanUp.runInstanceCleanup(instance);
			}

		});

	}

	@Override
	protected void prefillGUI() {
		boolean cleanUpFlag = (Boolean) dataStore.get("do_clean_up");
		doCleanUp.setSelected(cleanUpFlag);
		String cleanUpAction = (String) dataStore.get("clean_up_action");
		if (cleanUpAction.equals("archive")) cleanUpOptions[0].setSelected(true);
		else if (cleanUpAction.equals("delete")) cleanUpOptions[1].setSelected(true);
	}

	@Override
	protected void verifyInputs() throws Exception {
		dataStore.store("do_clean_up", doCleanUp.isSelected());
		dataStore.store("clean_up_action", cleanUpOptions[0].isSelected() ? "archive" : cleanUpOptions[1].isSelected() ? "delete" : "none");
	}
}
