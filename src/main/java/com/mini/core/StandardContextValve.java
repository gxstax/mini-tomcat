package com.mini.core;

import com.mini.*;
import com.mini.connector.http.HttpRequestImpl;
import com.mini.valves.ValveBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>
 * StandardContextValve（默认的servlet处理方式）
 * </p>
 *
 * @author Ant
 * @since 2024/3/18 9:18
 */
public class StandardContextValve extends ValveBase {

    private static final String info = "org.apache.catalina.core.StandardContextValve/1.0";

    private FilterDef filterDef = null;

    public StandardContextValve() {

    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void invoke(Request request, Response response, ValveContext valveContext)
            throws IOException, ServletException {
        System.out.println("StandardContextValve invoke()");
//        Servlet instance = ((StandardWrapper) getContainer()).getServlet();


        StandardWrapper servletWrapper = null;
        String uri = ((HttpRequestImpl) request).getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        String servletClassName = servletName;
        StandardContext context = (StandardContext) getContainer();
        servletWrapper = (StandardWrapper) context.getWrapper(servletName);

        try {
            System.out.println("Call service()");
            servletWrapper.invoke(request, response);
        } catch (Exception e) {
            System.out.println(e.toString());
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }

    /**
     * <p>
     * 根据context中的filter map信息挑选出符合模式的filter，创建filterChain
     * </p>
     *
     * @param request
     * @param servlet
     * @return com.mini.core.ApplicationFilterChain
     */
    private ApplicationFilterChain createFilterChain(Request request, Servlet servlet) {
        System.out.println("createFilterChain()");
        if (null == servlet) {
            return null;
        }

        ApplicationFilterChain filterChain = new ApplicationFilterChain();
        filterChain.setServlet(servlet);

        StandardWrapper wrapper = (StandardWrapper) getContainer();

        return filterChain;
    }
}
