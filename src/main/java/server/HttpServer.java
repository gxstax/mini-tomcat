package server;

import java.io.File;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/1/3 9:17
 */
public class HttpServer {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        ServletContainer container = new ServletContainer();

        // connector 和 container 相互引用
        container.setConnector(connector);
        connector.setContainer(container);

        connector.start(connector);
    }

}
