package org.canvacord.gui.wizard.cards;

/**
 * A BackgroundTaskCard is any entity that is makes use of a
 * BackgroundTask and wants to be notified of the result of
 * said task when it completes.
 * @param <T> the expected return value of the background task
 */
public interface BackgroundTaskCard<T> {

	void updateTask(int typeCode, T value);

}
