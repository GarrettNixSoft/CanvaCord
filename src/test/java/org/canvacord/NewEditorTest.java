package org.canvacord;

import org.canvacord.gui.options.EditInstancePanel;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.LookAndFeel;

public class NewEditorTest {

	public static void main(String[] args) {

		LookAndFeel.init();

		InstanceManager.loadInstances();
		Instance instance = InstanceManager.getInstances().get(0);

		EditInstancePanel editor = new EditInstancePanel(instance);
		editor.run();

	}

}
