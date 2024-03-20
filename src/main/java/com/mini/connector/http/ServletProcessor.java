package com.mini.connector.http;

import com.mini.Request;
import com.mini.Response;
import com.mini.connector.http.HttpConnector;
import com.mini.connector.http.HttpRequestImpl;
import com.mini.connector.http.HttpResponseImpl;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/1/3 17:50
 */
public class ServletProcessor {
    private HttpConnector connector;

    public ServletProcessor(HttpConnector connector) {
        this.connector = connector;
    }
    //响应头定义，里面包含变量
    private static String OKMessage = "HTTP/1.1 ${StatusCode} ${StatusName}\r\n" +
            "Content-Type: ${ContentType}\r\n" +
            "Server: minit\r\n" +
            "Date: ${ZonedDateTime}\r\n" +
            "\r\n";

    public void process(Request request, Response response) throws ServletException, IOException {
        this.connector.getContainer().invoke(request, response);
    }
}
