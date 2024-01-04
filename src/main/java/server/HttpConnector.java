package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <p>
 * tomcat连接器
 * </p>
 *
 * @author Ant
 * @since 2024/1/4 9:21
 */
public class HttpConnector implements Runnable {

    int minProcessors = 3;
    int maxProcessors = 10;
    int curProcessors = 0;

    // Processor池
    Deque<HttpProcessor> processors = new ArrayDeque<>();

    public void run() {
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
            try {
                socket = serverSocket.accept();
                HttpProcessor processor = createProcessor();
                if (null == processor) {
                    socket.close();
                    continue;
                }

                processor.assign(socket);

                // Close Socket
//                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start(HttpConnector connector) {
        Thread thread = new Thread(this);
        thread.start();
    }

    private HttpProcessor createProcessor() {
        synchronized (processors) {
            if (processors.size() > 0) {
                return  processors.pop();
            }

            if (curProcessors < maxProcessors) {
                return newProcessor();
            }
        }

        return null;
    }

    private HttpProcessor newProcessor() {
        HttpProcessor processor = new HttpProcessor(this);
        processor.start();
        processors.push(processor);
        curProcessors++;
        return processors.pop();
    }

    /**
     * <p>
     * 回收processor
     * </p>
     *
     * @return void
     */
    public void recycle(HttpProcessor processor) {
        // 使用完放回池子
        processors.push(processor);
    }
}
