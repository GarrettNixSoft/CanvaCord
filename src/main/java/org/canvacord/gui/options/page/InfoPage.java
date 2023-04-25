package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.options.OptionPage;

import javax.swing.*;

/**
 * An InfoPage represents an options page without any actual options
 * to modify. Instead, it serves to inform the user about a given
 * category of options menus. These are generally used as the page
 * representation for categories of menus (non-leaf nodes).
 */
public abstract class InfoPage extends OptionPage {

	public InfoPage(String name) {
		super(name);
	}

	// TODO implement methods to build a basic layout
	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("", "[grow]", "[]"));

		JLabel infoLabel = new JLabel();
		infoLabel.setText(getInfoText());
		infoLabel.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		add(infoLabel, "cell 0 0");

	}

	@Override
	protected void initLogic() {
		// TODO provide clickable links to sub pages?
	}

	@Override
	protected void prefillGUI() {
		// nothing
	}

	@Override
	protected void verifyInputs() throws Exception {
		// nothing
	}

	/**
	 * Provide the text that should be displayed on this page. For best
	 * results, wrap the text in HTML tags, which will allow HTML-style
	 * formatting of the text.
	 */
	protected abstract String getInfoText();

}
