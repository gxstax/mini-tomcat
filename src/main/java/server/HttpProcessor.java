package server;

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
    boolean available = false;
    HttpConnector connector;

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

    public void process(Socket socket) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        InputStream input = null;
        OutputStream output = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();

            // Create a request object and parse the input stream
            HttpRequest request = new HttpRequest(input);
            request.parse(socket);

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
