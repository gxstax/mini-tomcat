package com.mini.core;

import com.mini.connector.HttpRequestFacade;
import com.mini.connector.HttpResponseFacade;
import com.mini.connector.http.HttpRequestImpl;
import com.mini.connector.http.HttpResponseImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>
 * 过滤器调用链
 * </p>
 *
 * @author Ant
 * @since 2024/3/25 10:52
 */
public class ApplicationFilterChain implements FilterChain {

    private ArrayList<ApplicationFilterConfig> filters = new ArrayList<>();
    private Iterator<ApplicationFilterConfig> iterator = null;
    private Servlet servlet = null;

    public ApplicationFilterChain() {
        super();
    }

    /**
     * Causes the next filter in the chain to be invoked, or if the calling filter is the last filter
     * in the chain, causes the resource at the end of the chain to be invoked.
     *
     * @param request  the request to pass along the chain.
     * @param response the response to pass along the chain.
     * @throws IOException      if an I/O related error has occurred during the processing
     * @throws ServletException if an exception has occurred that interferes with the
     *                          filterChain's normal operation
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        System.out.println("FilterChain doFilter()");
        internalDoFilter(request, response);
    }

    private void internalDoFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (null == this.iterator) {
            this.iterator = filters.iterator();
        }

        if (this.iterator.hasNext()) {
            // 拿到下一个filter
            ApplicationFilterConfig filterConfig = iterator.next();
            Filter filter = null;
            try {
                filter = filterConfig.getFilter();
                filter.doFilter(request, response, this);
            } catch (IOException e) {
                throw e;
            } catch (ServletException e) {
                throw e;
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new ServletException("filterChain.filter", e);
            }
            return;
        }

        try {
            //最后调用servlet
            HttpServletRequest requestFacade = new HttpRequestFacade((HttpRequestImpl) request);
            HttpServletResponse responseFacade = new HttpResponseFacade((HttpResponseImpl) response);
            servlet.service(requestFacade, responseFacade);
        } catch (IOException e) {
            throw e;
        } catch (ServletException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServletException("filterChain.servlet", e);
        }
    }

    void addFilter(ApplicationFilterConfig filterConfig) {
        this.filters.add(filterConfig);
    }

    void release() {
        this.filters.clear();
        this.iterator = iterator;
        this.servlet = null;
    }

    void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }
}
