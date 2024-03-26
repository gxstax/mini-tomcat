package com.mini.startup;

import com.mini.Logger;
import com.mini.connector.http.HttpConnector;
import com.mini.core.ContainerListenerDef;
import com.mini.core.FilterDef;
import com.mini.core.FilterMap;
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
        Logger logger = new FileLogger();

        HttpConnector connector = new HttpConnector();
        StandardContext container = new StandardContext(logger);

        // connector 和 container 相互引用
        container.setConnector(connector);
        connector.setContainer(container);

        container.setLogger(logger);

        // 过滤器相关
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("TestFilter");
        filterDef.setFilterClass("test.TestFilter");
        container.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("TestFilter");
        filterMap.setURLPattern("/*");
        container.addFilterMap(filterMap);
        container.filterStart();

        // 监听器相关
        ContainerListenerDef listenerDef = new ContainerListenerDef();
        listenerDef.setListenerName("TestListener");
        listenerDef.setListenerClass("test.TestListener");

        container.addListenerDef(listenerDef);
        container.listenerStart();

        container.start();
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
