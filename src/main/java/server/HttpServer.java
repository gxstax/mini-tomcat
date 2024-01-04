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
        HttpServer server = new HttpServer();
        server.await();
    }

    /**
     * <p>
     * 服务器循环等待请求并处理
     * </p>
     *
     * @return void
     */
    public void await() {
        HttpConnector connector = new HttpConnector();
        connector.start(connector);
    }
}
