package com.mini;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 容器接口
 * </p>
 *
 * @author Ant
 * @since 2024/3/12 17:11
 */
public interface Container {
    public static final String ADD_CHILD_EVENT = "addChild";
    public static final String REMOVE_CHILD_EVENT = "removeChild";

    public String getInfo();

    public ClassLoader getLoader();

    public void setLoader(ClassLoader loader);

    public String getName();

    public void setName(String name);

    public Container getParent();

    public void setParent(Container container);

    public void addChild(Container child);

    public Container findChild(String name);

    public Container[] findChildren();

    public void invoke(Request request, Response response) throws IOException, ServletException;

    void removeChild(Container child);

    public Logger getLogger();

    public void setLogger(Logger logger);
}
