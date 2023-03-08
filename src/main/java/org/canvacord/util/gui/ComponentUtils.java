package org.canvacord.util.gui;

import javax.swing.*;
import java.awt.*;

public class ComponentUtils {


	public static void setComponentsEnabledRecursively(JComponent component, boolean enabled) {

		// set the flag for the target component
		component.setEnabled(enabled);

		// set the flag recursively for all subcomponents
		for (Component subcomponent : component.getComponents()) {
			if (subcomponent instanceof JComponent jComponent)
				setComponentsEnabledRecursively(jComponent, enabled);
		}

	}
}
