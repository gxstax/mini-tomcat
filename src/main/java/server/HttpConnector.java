package server;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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

    public static Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

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

    /**
     * Creates a new session and generates a unique session ID.
     *
     * @return the new session
     */
    public static Session createSession() {
        Session session = new Session();
        session.setValid(true);
        session.setCreationTime(System.currentTimeMillis());
        String sessionId = generateSessionId();
        sessions.put(sessionId, session);
        return session;
    }

    /**
     * Generates a random session ID.
     *
     * @return the generated session ID
     */
    protected static synchronized String generateSessionId() {
        Random random = new Random();
        long seed = System.currentTimeMillis();
        random.setSeed(seed);
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
            byte b2 = (byte) (bytes[i] & 0x0f);
            if (b1 < 10)
                result.append((char) ('0' + b1));
            else
                result.append((char) ('A' + (b1 - 10)));
            if (b2 < 10)
                result.append((char) ('0' + b2));
            else
                result.append((char) ('A' + (b2 - 10)));
        }
        return result.toString();
    }
}
