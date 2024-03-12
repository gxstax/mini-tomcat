package com.mini.startup;

import com.mini.connector.http.HttpConnector;
import com.mini.core.StandardContext;

import java.io.File;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/12 18:37
 */
public class Bootstrap {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        StandardContext container = new StandardContext();

        // connector 和 container 相互引用
        container.setConnector(connector);
        connector.setContainer(container);

        connector.start(connector);
    }
}
