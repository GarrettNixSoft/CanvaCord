package org.canvacord.gui.wizard.cards;

public interface BackgroundTaskCard<T> {

	void updateTask(int typeCode, T value);

}
