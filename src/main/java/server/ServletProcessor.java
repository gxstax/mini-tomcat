package server;

import org.apache.commons.lang3.text.StrSubstitutor;

import javax.servlet.Servlet;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/1/3 17:50
 */
public class ServletProcessor {
    //响应头定义，里面包含变量
    private static String OKMessage = "HTTP/1.1 ${StatusCode} ${StatusName}\r\n" +
            "Content-Type: ${ContentType}\r\n" +
            "Server: minit\r\n" +
            "Date: ${ZonedDateTime}\r\n" +
            "\r\n";

    public void process(HttpRequest request, Response response) {
        //首先根据uri最后一个/号来定位，后面的字符串认为是servlet名字
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;
        PrintWriter writer = null;

        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(HttpServer.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        // 获取PrintWriter
        try {
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //
        Class<?> servletClass = null;
        try {
            servletClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }

        // 写响应头
        String head = composeResponseHead();
        writer.println(head);

        //创建servlet新实例，然后调用service()，由它来写动态内容到响应体
        Servlet servlet = null;
        try {
            servlet = (Servlet) servletClass.newInstance();
            servlet.service(request, response);
        } catch (Exception e) {
            System.out.println(e.toString());
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }

    private String composeResponseHead() {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("StatusCode", "200");
        valuesMap.put("StatusName", "OK");
        valuesMap.put("ContentType", "text/html;charset=utf-8");
        valuesMap.put("ZonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now()));
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String responseHead = sub.replace(OKMessage);
        return responseHead;
    }
}
