package com.mini.core;

import com.mini.Context;
import com.mini.Request;
import com.mini.Response;
import com.mini.Wrapper;
import com.mini.connector.http.HttpConnector;
import com.mini.startup.Bootstrap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/12 18:38
 */
public class StandardContext extends ContainerBase implements Context {
    HttpConnector connector = null;

    // 包含servlet类和实例的map<servletName, ServletClassName>
    Map<String, String> servletClsMap = new ConcurrentHashMap<>();
    // map<servletName, Servlet>
    Map<String, StandardWrapper> servletInstanceMap = new ConcurrentHashMap<>();

    public StandardContext() {
        super();
        pipeline.setBasic(new StandardContextValve());

        try {
            // class loader 初始化
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Bootstrap.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        log("Container created.");
    }

    @Override
    public void invoke(Request request, Response response)
            throws ServletException, IOException {

        System.out.println("StandardContext invoke()");
        super.invoke(request, response);
    }

    @Override
    public String getInfo() {
        return "Mini Servlet Context, version 0.1";
    }

    public HttpConnector getConnector() {
        return connector;
    }
    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void setDisplayName(String displayName) {
    }

    @Override
    public String getDocBase() {
        return null;
    }

    @Override
    public void setDocBase(String docBase) {
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void setPath(String path) {
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int timeout) {
    }

    @Override
    public String getWrapperClass() {
        return null;
    }

    @Override
    public void setWrapperClass(String wrapperClass) {
    }

    @Override
    public Wrapper createWrapper() {
        return null;
    }

    public Wrapper getWrapper(String name) {
        StandardWrapper servletWrapper = servletInstanceMap.get(name);
        if (null == servletWrapper) {
            String servletClassName = name;
            servletWrapper = new StandardWrapper(servletClassName, this);
            this.servletClsMap.put(name, servletClassName);
            this.servletInstanceMap.put(name, servletWrapper);
        }
        return servletWrapper;
    }

    @Override
    public String findServletMapping(String pattern) {
        return null;
    }

    @Override
    public String[] findServletMappings() {
        return null;
    }

    @Override
    public void reload() {
    }
}
