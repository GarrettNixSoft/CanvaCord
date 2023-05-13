package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;

import javax.swing.*;

public class CleanUpPage extends OptionPage {

	private JCheckBox doCleanUp;
	private JRadioButton[] cleanUpOptions;

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

		// Preselect default
		cleanUpOptions[0].setSelected(true);

	}

	@Override
	protected void initLogic() {
		// TODO
	}

	@Override
	protected void prefillGUI() {
		// TODO
	}

	@Override
	protected void verifyInputs() throws Exception {
		// TODO
	}
}
