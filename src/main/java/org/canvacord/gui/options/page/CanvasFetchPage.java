package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.instance.Instance;

public class CanvasFetchPage extends OptionPage {

	public CanvasFetchPage() {
		super("Canvas Fetching");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("", "[]", "[]"));



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
