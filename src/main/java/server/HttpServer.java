package server;

import jdk.internal.util.xml.impl.Input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/1/3 9:17
 */
public class HttpServer {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    /**
     * <p>
     * 服务器循环等待请求并处理
     * </p>
     *
     * @return void
     */
    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;

        try {
            // Create a server socket on port 8080 and bind it to localhost
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (true) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                // Accept incoming connection
                socket = serverSocket.accept();

                // Get input and output streams from the socket
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // Create a request object and parse the input stream
                Request request = new Request(input);
                request.parse();

                // Create a response object and set the request
                Response response = new Response(output);
                response.setRequest(request);

                if (request.getUri().startsWith("/servlet/")) {
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request, response);
                } else {
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }

                // Close the socket
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
