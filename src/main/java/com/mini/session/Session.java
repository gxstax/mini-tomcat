package com.mini.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Session
 * </p>
 *
 * @author Ant
 * @since 2024/1/8 14:28
 */
public class Session implements HttpSession {

    private String sessionid;
    private long creationTime;
    private boolean valid;
    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public String getId() {
        return this.sessionid;
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.attributes.keySet());
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        this.attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {
        this.valid = false;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    public void setCreationTime(long currentTimeMillis) {
        this.creationTime = currentTimeMillis;
    }

    public void setId(String sessionId) {
        this.sessionid = sessionId;
    }
}
