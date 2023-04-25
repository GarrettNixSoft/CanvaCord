package org.canvacord.util;

import javax.swing.*;

public class CanvaCordModels {

	public static SpinnerNumberModel getGenericNumberModel() {
		return new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
	}

	public static SpinnerNumberModel getMinutesModel() {
		return new SpinnerNumberModel(0, 0, 59, 1);
	}

	public static SpinnerNumberModel getHoursModel() {
		return new SpinnerNumberModel(12, 1, 12, 1);
	}

	public static JSpinner.NumberEditor getMinutesEditor(JSpinner spinner) {
		return new JSpinner.NumberEditor(spinner, "00");
	}

}
