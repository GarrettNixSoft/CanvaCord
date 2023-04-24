package org.canvacord.gui.options.card;

import org.canvacord.gui.options.OptionPage;
import org.canvacord.instance.Instance;

public abstract class InstanceOptionsPage extends OptionPage {

	protected final Instance instanceToEdit;

	public InstanceOptionsPage(String name, Instance instanceToEdit) {
		super(name);
		this.instanceToEdit = instanceToEdit;
	}

}
