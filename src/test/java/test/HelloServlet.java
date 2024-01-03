package test;

import server.Request;
import server.Response;
import server.Servlet;

import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author GaoXin
 * @since 2024/1/3 18:18
 */
public class HelloServlet implements Servlet {
    @Override
    public void service(Request request, Response response) throws IOException {
        String doc = "<!DOCTYPE html> \n" +
                "<html>\n" + "<head><meta charset=\"utf-8\"><title>Test</title></head>\n" +
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">" +
                "Hello World 你好" +
                "</h1>\n";
        response.getOutput().write(doc.getBytes("utf-8"));
    }
}
