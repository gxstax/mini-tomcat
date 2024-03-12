package server;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * ServletWrapper（用来管理servlet的生命周期）
 * </p>
 *
 * @author Ant
 * @since 2024/3/11 17:20
 */
public class ServletWrapper {
    private Servlet instance = null;

    private String servletClass;
    private ClassLoader loader;
    private String name;
    protected ServletContainer parent = null;

    public ServletWrapper(String servletClass, ServletContainer parent) {
        this.parent = parent;
        this.servletClass = servletClass;
        try {
            loadServlet();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    public Servlet loadServlet() throws ServletException {
        if (null != instance) {
            return instance;
        }

        Servlet servlet = null;
        String actualClass = servletClass;
        if (null == actualClass) {
            throw new ServletException("servlet class has not been specified");
        }

        ClassLoader classLoader = getLoader();
        Class classClass = null;

        try {
            if (null != classLoader) {
                classClass = classLoader.loadClass(actualClass);
            }
        } catch (ClassNotFoundException e) {
            throw new ServletException("Servlet class not found");
        }

        try {
            servlet = (Servlet) classClass.newInstance();
        } catch (Throwable e) {
            throw new ServletException("Failed to instantiate servlet");
        }

        servlet.init(null);

        instance = servlet;
        return servlet;
    }

    public void invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (null != instance) {
            instance.service(request, response);
        }
    }

    public ClassLoader getLoader() {
        if (null != loader) {
            return loader;
        }
        return parent.getLoader();
    }

    public String getServletClass() {
        return servletClass;
    }

    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    public ServletContainer getParent() {
        return parent;
    }

    public void setParent(ServletContainer parent) {
        this.parent = parent;
    }

    public Servlet getServlet() {
        return this.instance;
    }
}
