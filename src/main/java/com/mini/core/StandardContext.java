package com.mini.core;

import com.mini.*;
import com.mini.connector.http.HttpConnector;
import com.mini.startup.Bootstrap;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Container 容器
 * </p>
 *
 * @author Ant
 * @since 2024/3/12 18:38
 */
public class StandardContext extends ContainerBase implements Context {
    // 下面的属性记录了filter的配置
    private Map<String, ApplicationFilterConfig> filterConfigs = new ConcurrentHashMap<>();
    private Map<String, FilterDef> filterDefs = new ConcurrentHashMap<>();
    private FilterMap filterMaps[] = new FilterMap[0];

    // 连接器
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
    }

    public StandardContext(Logger logger) {
        this();
        this.logger = logger;
        this.name = Thread.currentThread().getName();
        log("Container created.");
    }

    public void addFilterDef(FilterDef filterDef) {
        filterDefs.put(filterDef.getFilterName(), filterDef);
    }

    public void addFilterMap(FilterMap filterMap) {
        // validate the proposed filter mapping
        String filterName = filterMap.getFilterName();
        String servletName = filterMap.getServletName();
        String urlPattern = filterMap.getURLPattern();

        if (null == findFilterDef(filterName)) {
            throw new IllegalArgumentException("standardContext.filterMap.name" + filterName);
        }
        if (null == servletName && null == urlPattern) {
            throw new IllegalArgumentException("standardContext.filterMap.either");
        }
        if ((servletName != null) && (urlPattern != null)) {
            throw new IllegalArgumentException("standardContext.filterMap.either");
        }
        if ((urlPattern != null) && !validateURLPattern(urlPattern)) {
            throw new IllegalArgumentException("standardContext.filterMap.pattern"+urlPattern);
        }

        // Add this filter mapping to our registered set
        synchronized (filterMaps) {
            FilterMap results[] = new FilterMap[filterMaps.length + 1];
            System.arraycopy(filterMaps, 0, results, 0, filterMaps.length);
            results[filterMaps.length] = filterMap;
            filterMaps = results;
        }
    }

    private FilterDef findFilterDef(String filterName) {
        return filterDefs.get(filterName);
    }

    private FilterDef[] findFilterDefs() {
        synchronized (filterDefs) {
            FilterDef[] results = new FilterDef[filterDefs.size()];
            return filterDefs.values().toArray(results);
        }
    }

    public FilterMap[] findFilterMaps() {
        return filterMaps;
    }

    public void removeFilterDef(FilterDef filterDef) {
        filterDefs.remove(filterDef.getFilterName());
    }

    public void removeFilterMap(FilterMap filterMap) {
        synchronized (filterMaps) {
            // 确保当前存在这个过滤器映射
            int n = -1;
            for (int i = 0; i < filterMaps.length; i++) {
                if (filterMaps[i] == filterMap) {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            // 删除指定的过滤器映射
            FilterMap results[] = new FilterMap[filterMaps.length - 1];
            System.arraycopy(filterMaps, 0, results, 0, n);
            System.arraycopy(filterMaps, n + 1, results, n, (filterMaps.length - 1) - n);
            filterMaps = results;
        }
    }

    // 对配置好的所有filter名字，创建实例，存储在filterConfigs中，可以生效了
    public boolean filterStart() {
        System.out.println("Filter Start..........");
        // 为每个定义的过滤器实例化并记录一个FilterConfig
        boolean ok = true;
        synchronized (filterConfigs) {
            filterConfigs.clear();
            Iterator<String> names = filterDefs.keySet().iterator();
            while (names.hasNext()) {
                String name = names.next();
                ApplicationFilterConfig filterConfig = null;
                try {
                    filterConfig = new ApplicationFilterConfig(this, (FilterDef) filterDefs.get(name));
                    filterConfigs.put(name, filterConfig);
                } catch (Throwable t) {
                    ok = false;
                }
            }
        }
        return (ok);
    }

    public FilterConfig findFilterConfig(String name) {
        return (filterConfigs.get(name));
    }

    private boolean validateURLPattern(String urlPattern) {
        if (urlPattern == null) {
            return (false);
        }
        if (urlPattern.startsWith("*.")) {
            if (urlPattern.indexOf('/') < 0) {
                return (true);
            } else {
                return (false);
            }
        }
        if (urlPattern.startsWith("/")) {
            return (true);
        } else {
            return (false);
        }
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
