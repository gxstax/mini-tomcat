package server;

import javax.servlet.Servlet;
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
 * 容器（负责管理Servlet）
 * </p>
 *
 * @author Ant
 * @since 2024/3/11 17:18
 */
public class ServletContainer {

    HttpConnector connector = null;

    ClassLoader loader = null;

    // 包含servlet类和实例的map<servletName, ServletClassName>
    Map<String, String> servletClsMap = new ConcurrentHashMap<>();
    // map<servletName, Servlet>
    Map<String, ServletWrapper> servletInstanceMap = new ConcurrentHashMap<>();

    public ServletContainer() {
        // class loader 初始化
        URL[] urls = new URL[1];
        URLStreamHandler streamHandler = null;
        File classPath = new File(HttpServer.WEB_ROOT);
        try {
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public String getInfo() {
        return null;
    }

    public ClassLoader getLoader() {
        return this.loader;
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public HttpConnector getConnector() {
        return connector;
    }

    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }

    public String getName() {
        return null;
    }

    public void setName(String name) {

    }

    /**
     * <p>
     * 从map中找到相关的servlet，然后调用
     * </p>
     *
     * @param request
     * @param response
     * @return void
     */
    public void invoke(HttpRequest request, HttpResponse response) throws ServletException {
        ServletWrapper servletWrapper = null;
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf(";"));
        String servletClassName = servletName;
        servletWrapper = servletInstanceMap.get(servletName);
        // 如果容器中没有这个servlet，先要load类，创建新实例
        if (servletWrapper == null) {
            servletWrapper = new ServletWrapper(servletClassName, this);
            this.servletClsMap.put(servletName, servletClassName);
            this.servletInstanceMap.put(servletName, servletWrapper);
        }

        // 调用service()
        HttpRequestFacade requestFacade = new HttpRequestFacade(request);
        HttpResponseFacade responseFacade = new HttpResponseFacade(response);
        System.out.println("Call service()");
        try {
            servletWrapper.invoke(requestFacade, responseFacade);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
