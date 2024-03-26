package com.mini;

import java.util.EventObject;

/**
 * <p>
 * 容器事件对象
 * </p>
 *
 * @author Ant
 * @since 2024/3/26 13:57
 */
public class ContainerEvent extends EventObject {

    private Container container;
    private Object data;
    private String type;

    public ContainerEvent(Container container, String type, Object data) {
        super(container);
        this.container = container;
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public Container getContainer() {
        return this.container;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "ContainerEvent{" +
                "container=" + container +
                ", data=" + data +
                ", type='" + type + '\'' +
                '}';
    }
}
