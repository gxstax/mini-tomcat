package server;

import java.io.IOException;

/**
 * <p>
 * Servlet 接口
 * </p>
 *
 * @author Ant
 * @since 2024/1/3 17:47
 */
public interface Servlet {
    public void service(Request request, Response response) throws IOException;
}
