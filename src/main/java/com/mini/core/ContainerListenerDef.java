package com.mini.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 容器监听定义
 * </p>
 *
 * @author Ant
 * @since 2024/3/26 16:43
 */
public class ContainerListenerDef {
    private String description;
    private String displayName;
    private String listenerClass;
    private String listenerName;
    private Map<String,String> parameters = new ConcurrentHashMap<>();

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getListenerClass() {
        return listenerClass;
    }

    public void setListenerClass(String listenerClass) {
        this.listenerClass = listenerClass;
    }

    public String getListenerName() {
        return listenerName;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void addInitParameter(String name, String value) {
        parameters.put(name, value);
    }

    @Override
    public String toString() {
        return "ContainerListenerDef{" +
                "description='" + description + '\'' +
                ", displayName='" + displayName + '\'' +
                ", listenerClass='" + listenerClass + '\'' +
                ", listenerName='" + listenerName + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
