package org.canvacord.gui.options.page;

import org.canvacord.instance.Instance;

/**
 * An InfoPage represents an options page without any actual options
 * to modify. Instead, it serves to inform the user about a given
 * category of options menus. These are generally used as the page
 * representation for categories of menus (non-leaf nodes).
 */
public abstract class InfoPage extends InstanceOptionsPage {

	public InfoPage(String name, Instance instanceToEdit) {
		super(name, instanceToEdit);
	}

	// TODO implement methods to build a basic layout

	/**
	 * Provide the text that should be displayed on this page. For best
	 * results, wrap the text in HTML tags, which will allow HTML-style
	 * formatting of the text.
	 * @param textBlock the text to display on this page
	 */
	protected abstract void setInfoText(String textBlock);

}
