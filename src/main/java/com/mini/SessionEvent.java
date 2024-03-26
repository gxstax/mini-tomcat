package com.mini;

import java.util.EventObject;

/**
 * <p>
 * session 事件
 * </p>
 *
 * @author Ant
 * @since 2024/3/26 16:11
 */
public final class SessionEvent extends EventObject {
    private Object data;
    private Session session;
    private String type;

    public SessionEvent(Session session, String type, Object data) {
        super(session);
        this.session = session;
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public Session getSession() {
        return session;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SessionEvent{" +
                "data=" + data +
                ", session=" + session +
                ", type='" + type + '\'' +
                '}';
    }
}
