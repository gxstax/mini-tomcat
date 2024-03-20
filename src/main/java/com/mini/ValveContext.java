package com.mini;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>
 * ValveContext
 * </p>
 *
 * @author Ant
 * @since 2024/3/13 8:58
 */
public interface ValveContext {
    public String getInfo();

    public void invokeNext(Request request, Response response) throws IOException, ServletException;
}
