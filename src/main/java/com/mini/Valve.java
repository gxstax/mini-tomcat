package com.mini;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>
 * Value接口
 * </p>
 *
 * @author Ant
 * @since 2024/3/13 8:58
 */
public interface Valve {
    public String getInfo();

    public Container getContainer();

    public void setContainer(Container container);

    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException;
}
