package com.mini.core;

import com.mini.Request;
import com.mini.Response;
import com.mini.ValveContext;
import com.mini.connector.HttpRequestFacade;
import com.mini.connector.HttpResponseFacade;
import com.mini.connector.http.HttpRequestImpl;
import com.mini.connector.http.HttpResponseImpl;
import com.mini.valves.ValveBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/18 16:43
 */
public class StandardWrapperValve extends ValveBase {

    @Override
    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {

        System.out.println("StandardWrapperValue invoke()");
        Servlet instance = ((StandardWrapper) getContainer()).getServlet();

        ApplicationFilterChain filterChain = createFilterChain(request, instance);
        if ((null != instance) && (null != filterChain)) {
            filterChain.doFilter((ServletRequest) request, (ServletResponse) response);
        }
        filterChain.release();


        // TODO Auto-generated method stub
//        System.out.println("StandardWrapperValue invoke()");
//        HttpServletRequest requestFacade = new HttpRequestFacade((HttpRequestImpl) request);
//        HttpServletResponse responseFacade = new HttpResponseFacade((HttpResponseImpl)response);
//        Servlet instance = ((StandardWrapper) getContainer()).getServlet();
//
//        if (null != instance) {
//            instance.service(requestFacade, responseFacade);
//        }
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
        StandardContext context = (StandardContext) wrapper.getParent();

        // 从context中拿到filter的信息
        FilterMap filterMaps[] = context.findFilterMaps();
        if ((null == filterMaps) || (filterMaps.length == 0)) {
            return filterChain;
        }

        // 要匹配的路径
        String requestPath = null;
        if (request instanceof HttpServletRequest) {
            String contextPath = "";
            String requestURI = ((HttpRequestImpl) request).getUri();
            if (requestURI.length() >= contextPath.length()) {
                requestPath = requestURI.substring(contextPath.length());
            }
        }

        // 要匹配的servlet名
        String servletName = wrapper.getName();
        // 下面遍历 filter Map,找到匹配URL模式的filter，加入到filterChain中
        int n = 0;
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchFiltersURL(filterMaps[i], requestPath)) {
                continue;
            }
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)
                    context.findFilterConfig(filterMaps[i].getFilterName());
            if (null == filterConfig) {
                continue;
            }

            filterChain.addFilter(filterConfig);
            n++;
        }

        // 下面遍历 filter Map，找到匹配 servlet 的 filter，加入到 filterChain 中
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchFiltersServlet(filterMaps[i], servletName)) {
                continue;
            }
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)
                    context.findFilterConfig(filterMaps[i].getFilterName());
            if (null == filterConfig) {
                continue;
            }

            filterChain.addFilter(filterConfig);
            n++;
        }

        return filterChain;
    }

    //字符串模式匹配filter的过滤路径
    private boolean matchFiltersURL(FilterMap filterMap, String requestPath) {
        if (requestPath == null) {
            return (false);
        }
        String testPath = filterMap.getURLPattern();
        if (testPath == null) {
            return (false);
        }
        if (testPath.equals(requestPath)) {
            return (true);
        }
        if (testPath.equals("/*")) {
            return (true);
        }
        if (testPath.endsWith("/*")) {
            // 路径符合/前缀，通配成功
            String comparePath = requestPath;
            while (true) {
                // 以/截取前段字符串，循环匹配
                if (testPath.equals(comparePath + "/*")) {
                    return (true);
                }
                int slash = comparePath.lastIndexOf('/');
                if (slash < 0) {
                    break;
                }
                comparePath = comparePath.substring(0, slash);
            }
            return (false);
        }
        if (testPath.startsWith("*.")) {
            int slash = requestPath.lastIndexOf('/');
            int period = requestPath.lastIndexOf('.');
            if ((slash >= 0) && (period > slash)) {
                return (testPath.equals("*." + requestPath.substring(period + 1)));
            }
        }
        return (false); // NOTE - Not relevant for selecting filters
    }

    private boolean matchFiltersServlet(FilterMap filterMap, String servletName) {
        if (servletName == null) {
            return (false);
        } else {
            return (servletName.equals(filterMap.getServletName()));
        }
    }
}
