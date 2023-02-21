package org.canvacord.event;

import java.util.ArrayList;
import java.util.List;

public class CanvaCordEventHandler {

    private static final List<CanvaCordEventListener> eventListeners;

    static {
        eventListeners = new ArrayList<>();
    }

    public static void addEventListener(CanvaCordEventListener listener) {
        eventListeners.add(listener);
    }

    public static void removeEventListener(CanvaCordEventListener listener) {
        eventListeners.remove(listener);
    }

    protected static void publishEvent(CanvaCordEvent event) {
        for (CanvaCordEventListener listener : eventListeners) {
            new Thread(
                    () -> listener.onEvent(event)
            ).start();
        }
    }

}
