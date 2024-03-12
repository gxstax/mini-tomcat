package com.mini.connector.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * <p>
 * Servlet处理器
 * </p>
 *
 * @author Ant
 * @since 2024/1/4 9:21
 */
public class HttpProcessor implements Runnable {
    Socket socket;
    // 是否正在处理
    boolean available = false;
    HttpConnector connector;

    private boolean keepAlive = false;
    private boolean http11 = true;

    public HttpProcessor() {
    }

    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            Socket socket = await();
            if (null == socket) {
                continue;
            }
            process(socket);

            // 完成此请求
            connector.recycle(this);
        }
    }

    /**
     * <p>
     * 处理具体的请求
     * </p>
     *
     * @param socket
     * @return void
     */
    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            keepAlive = true;
            while (keepAlive) {
                // Create a request object and parse the input stream
                HttpRequestImpl request = new HttpRequestImpl(input);

                // Create a response object and set the request
                HttpResponseImpl response = new HttpResponseImpl(output);
                response.setRequest(request);
                request.setResponse(response);

                // 应用层的协议解析
                request.parse(socket);

                // handle session
                if (null == request.getSessionId() || request.getSessionId().equals("")) {
                    request.getSession(true);
                }

                response.sendHeaders();

                if (request.getUri().startsWith("/servlet/")) {
                    ServletProcessor processor = new ServletProcessor(this.connector);
                    processor.process(request, response);
                } else {
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }

                finishResponse(response);
                System.out.println("response header connection------" + response.getHeader("Connection"));
                if ("close".equals(response.getHeader("Connection"))) {
                    keepAlive = false;
                }
            }


            // Close the socket
            socket.close();
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finishResponse(HttpResponseImpl response) {
        response.finishResponse();
    }

    /**
     * <p>
     * synchronized：当前处理器对象同步，确保一个处理一次只会绑定一个socket对象
     * </p>
     *
     * @param socket
     * @return void
     */
    public synchronized void assign(Socket socket) {
        // available 为true 说明正在处理中，需要等待
        while (available) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 存储新的可用 Socket，并通知我们的线程处理
        this.socket = socket;
        available = true;
        notifyAll();
    }

    /**
     * <p>
     * 阻塞等待 socket
     * （临界区为当前对象）
     * </p>
     *
     * @return java.net.Socket
     */
    private synchronized Socket await() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // available 为true，表明可以处理请求
        Socket socket = this.socket;
        available = false;

        // 唤醒其它线程
        notifyAll();
        return socket;
    }
}
