package org.canvacord.gui.task;

/**
 * A background task is meant to be something executed
 * in the background on its own thread so that the GUI
 * remains interactive while some work is being done.
 * @param <T> the type that should be returned by the {@code execute()} method
 */
public interface BackgroundTask<T> {

    T execute();

}
