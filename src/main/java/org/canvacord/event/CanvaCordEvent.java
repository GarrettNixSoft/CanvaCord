package org.canvacord.event;

public class CanvaCordEvent {

    public enum Type {
        NEW_INSTANCE, INSTANCE_DELETED,
        INITIALIZE_STARTED, INITIALIZE_UPDATE, INITIALIZE_COMPLETED,
        INSTANCE_STARTED, INSTANCE_STOPPED,
        FETCH_STARTED, FETCH_UPDATE, FETCH_COMPLETED,
        NOTIFY_STARTED, NOTIFY_UPDATE, NOTIFY_COMPLETED
    }

    private final Type type;
    private final Object payload;

    private CanvaCordEvent(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
        CanvaCordEventHandler.publishEvent(this);
    }

    public static void newEvent(Type type, Object payload) {
        new CanvaCordEvent(type, payload);
    }

    public Type getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
