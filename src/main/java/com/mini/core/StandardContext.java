package com.mini.core;

import com.mini.Context;
import com.mini.Wrapper;
import com.mini.connector.HttpRequestFacade;
import com.mini.connector.http.HttpConnector;
import com.mini.connector.http.HttpRequestImpl;
import com.mini.connector.HttpResponseFacade;
import com.mini.startup.Bootstrap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        // class loader 初始化
        URL[] urls = new URL[1];
        URLStreamHandler streamHandler = null;
        File classPath = new File(Bootstrap.WEB_ROOT);
        try {
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response) {
        StandardWrapper servletWrapper = null;
        String uri = ((HttpRequestImpl)request).getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        String servletClassName = servletName;

        servletWrapper = servletInstanceMap.get(servletName);
        if ( servletWrapper == null) {
            servletWrapper = new StandardWrapper(servletClassName,this);
            //servletWrapper.setParent(this);

            this.servletClsMap.put(servletName, servletClassName);
            this.servletInstanceMap.put(servletName, servletWrapper);
        }

        try {
            HttpServletRequest requestFacade = new HttpRequestFacade(request);
            HttpServletResponse responseFacade = new HttpResponseFacade(response);
            System.out.println("Call service()");

            servletWrapper.invoke(requestFacade, responseFacade);
        } catch (Exception e) {
            System.out.println(e.toString());
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public String getInfo() {
        return "Mini Servlet Context, vesion 0.1";
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
