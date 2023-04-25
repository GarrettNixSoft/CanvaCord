package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.instance.Instance;

import javax.swing.*;

public class NameIconPage extends InstanceOptionsPage {

	public NameIconPage(Instance instanceToEdit) {
		super("Name and Icon", instanceToEdit);
	}

	@Override
	protected void buildGUI() {
		// TODO
		setLayout(new MigLayout("", "[]", "[]"));
		JLabel label = new JLabel("Test");
		label.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		add(label, "cell 0 0");
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
	protected void verifyInputs() throws CanvaCordException {
		// TODO
	}
}
