package org.canvacord.event;

import org.canvacord.util.string.StringConverter;

public class CanvaCordEvent {

    public enum Type {
        NEW_INSTANCE, INSTANCE_DELETED,
        INITIALIZE_STARTED, INITIALIZE_UPDATE, INITIALIZE_COMPLETED,
        INSTANCE_STARTED, INSTANCE_STOPPED,
        FETCH_STARTED, FETCH_UPDATE, FETCH_COMPLETED, FETCH_ERROR,
        NOTIFY_STARTED, NOTIFY_UPDATE, NOTIFY_COMPLETED, NOTIFY_ERROR,
        NEW_ASSIGNMENT, NEW_ANNOUNCEMENT, ASSIGNMENT_DUE_DATE_APPROACHING,
        ASSIGNMENT_DUE_DATE_CHANGED,
        GUI_COMPONENT_CLICKED;

        public static Type stringToType(String typeStr) {
            for (Type type : values()) {
                if (type.toString().equals(typeStr))
                    return type;
            }
            return null;
        }

        @Override
        public String toString() {
            return StringConverter.enumToString(super.toString());
        }
    }

    public static Type[] NOTIFICATION_EVENTS = {
            Type.NEW_ASSIGNMENT,
            Type.NEW_ANNOUNCEMENT,
            Type.ASSIGNMENT_DUE_DATE_APPROACHING,
            Type.ASSIGNMENT_DUE_DATE_CHANGED
    };

    private final Type type;
    private final Object[] payload;

    private CanvaCordEvent(Type type, Object... payload) {
        this.type = type;
        this.payload = payload;
        CanvaCordEventHandler.publishEvent(this);
    }

    public static void newEvent(Type type, Object... payload) {
        new CanvaCordEvent(type, payload);
    }

    public Type getType() {
        return type;
    }

    public Object[] getPayload() {
        return payload;
    }
}
