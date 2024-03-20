package com.mini.startup;

import com.mini.Logger;
import com.mini.connector.http.HttpConnector;
import com.mini.core.StandardContext;
import com.mini.logger.FileLogger;

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

    public static int debug = 0;


    public static void main(String[] args) {
        if (debug >= 0) {
            log(".... startup ....");
        }
        HttpConnector connector = new HttpConnector();
        StandardContext container = new StandardContext();

        // connector 和 container 相互引用
        container.setConnector(connector);
        connector.setContainer(container);

        Logger logger = new FileLogger();
        container.setLogger(logger);

        connector.start(connector);
    }

    private static void log(String message) {
        System.out.print("Bootstrap: ");
        System.out.println(message);
    }

    private static void log(String message, Throwable exception) {
        log(message);
        exception.printStackTrace(System.out);
    }
}
