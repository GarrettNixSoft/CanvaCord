package org.canvacord.gui.wizard;

public abstract class BackgroundTaskWizard<T> extends CanvaCordWizard {

	public BackgroundTaskWizard(String title) {
		super(title);
	}

	public abstract void updateTask(int typeCode, T value);

}
