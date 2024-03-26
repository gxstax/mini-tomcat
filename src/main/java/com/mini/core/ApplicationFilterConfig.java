package com.mini.core;

import com.mini.Context;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/25 10:25
 */
public class ApplicationFilterConfig implements FilterConfig {

    private Context context = null;
    private Filter filter = null;
    private FilterDef filterDef = null;

    public ApplicationFilterConfig(Context context, FilterDef filterDef)
            throws ServletException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super();
        this.context = context;
        setFilterDef(filterDef);
    }

    /**
     * Returns the filter-name of this filter as defined in the deployment
     * descriptor.
     *
     * @return the filter name of this filter
     */
    @Override
    public String getFilterName() {
        return filterDef.getFilterName();
    }

    /**
     * Returns a reference to the {@link ServletContext} in which the caller
     * is executing.
     *
     * @return a {@link ServletContext} object, used by the caller to
     * interact with its servlet container
     * @see ServletContext
     */
    @Override
    public ServletContext getServletContext() {
        return this.context.getServletContext();
    }

    /**
     * Returns a <code>String</code> containing the value of the
     * named initialization parameter, or <code>null</code> if
     * the initialization parameter does not exist.
     *
     * @param name a <code>String</code> specifying the name of the
     *             initialization parameter
     * @return a <code>String</code> containing the value of the
     * initialization parameter, or <code>null</code> if
     * the initialization parameter does not exist
     */
    @Override
    public String getInitParameter(String name) {
        Map map = filterDef.getParameterMap();
        if (null == map) {
            return null;
        }
        return (String) map.get(name);
    }

    /**
     * Returns the names of the filter's initialization parameters
     * as an <code>Enumeration</code> of <code>String</code> objects,
     * or an empty <code>Enumeration</code> if the filter has
     * no initialization parameters.
     *
     * @return an <code>Enumeration</code> of <code>String</code> objects
     * containing the names of the filter's initialization parameters
     */
    @Override
    public Enumeration<String> getInitParameterNames() {
        Map<String, String> map = filterDef.getParameterMap();
        if (null == map) {
            return Collections.enumeration(new ArrayList<>());
        }
        return Collections.enumeration(map.keySet());
    }

    Filter getFilter()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, ServletException {
        if (null != this.filter) {
            return filter;
        }

        // 确定使用的类加载器
        String filterClass = filterDef.getFilterClass();
        ClassLoader classLoader = null;
        classLoader = context.getLoader();
        ClassLoader oldCtxClassLoader = Thread.currentThread().getContextClassLoader();

        // 实例化这个过滤器的新实例并返回
        Class clazz = classLoader.loadClass(filterClass);
        this.filter = (Filter) clazz.newInstance();
        filter.init(this);
        return this.filter;
    }

    FilterDef getFilterDef() {
        return this.filterDef;
    }

    void setFilterDef(FilterDef filterDef)
            throws ServletException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.filterDef = filterDef;
        if (null == filterDef) {
            // 释放之前分配的所有过滤器实例
            if (null != this.filter) {
                this.filter.destroy();
            }
            this.filter = null;
        } else {
            // 分配新的过滤器实例
            getFilter();
        }
    }

    void release() {
        if (null != this.filter) {
            filter.destroy();
        }
        this.filter = null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ApplicationFilterConfig[");
        sb.append("name=");
        sb.append(filterDef.getFilterName());
        sb.append(", filterClass=");
        sb.append(filterDef.getFilterClass());
        sb.append("]");
        return (sb.toString());
    }
}
