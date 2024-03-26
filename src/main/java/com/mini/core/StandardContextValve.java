package com.mini.core;

import com.mini.*;
import com.mini.connector.http.HttpRequestImpl;
import com.mini.valves.ValveBase;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
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


}