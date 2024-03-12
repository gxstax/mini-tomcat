package com.mini.connector.http;

/**
 * <p>
 * Http Request 头信息行抽象
 * </p>
 *
 * @author Ant
 * @since 2024/1/4 17:36
 */
public class HttpRequestLine {
    public static final int INITIAL_METHOD_SIZE = 8;
    public static final int INITIAL_URI_SIZE = 128;
    public static final int INITIAL_PROTOCOL_SIZE = 8;
    public static final int MAX_METHOD_SIZE = 32;
    public static final int MAX_URI_SIZE = 2048;
    public static final int MAX_PROTOCOL_SIZE = 32;

    /*
     * 下面的属性对应于Http Request规范，即头行格式 'method' 'uri' 'protocol'
     * 如：GET /hello.txt HTTP/1.1
     * char[] 存储每段的字符串，对应的xxxEnd的int值存储的是每段的结束位置
     */
    public char[] method;
    public int methodEnd;

    public char[] uri;
    public int uriEnd;

    public char[] protocol;
    public int protocolEnd;

    public HttpRequestLine() {
        this(new char[INITIAL_METHOD_SIZE], 0, new char[INITIAL_URI_SIZE], 0,
                new char[INITIAL_PROTOCOL_SIZE], 0);
    }

    public HttpRequestLine(char[] method, int methodEnd,
                           char[] uri, int uriEnd,
                           char[] protocol, int protocolEnd) {
        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    public void recycle() {
        methodEnd = 0;
        uriEnd = 0;
        protocolEnd = 0;
    }

    public int indexOf(char[] buf) {
        return indexOf(buf, buf.length);
    }

    /**
     * <p>
     * 这是主要的方法
     * 在uri[]中查找字符串buf的出现位置
     * </p>
     *
     * @param buf
     * @param end
     * @return int
     */
    public int indexOf(char[] buf, int end) {
        char firstChar = buf[0];
        // pos是查找字符串buf在uri[]中的开始位置
        int pos = 0;
        while (pos < uriEnd) {
            // 首字符定位开始位置
            pos = indexOf(firstChar, pos);
            if (pos == -1) {
                return -1;
            }
            if ((uriEnd - pos) < end) {
                return -1;
            }
            // 从开始位置起逐个字符比对
            for (int i = 0; i < end; i++) {
                if (uri[i + pos] != buf[i]) {
                    break;
                }
                // 每个字符都相等，则匹配上了，返回开始位置
                if (i == (end - 1)) {
                    return pos;
                }
            }
            pos++;
        }
        return -1;
    }

    public int indexOf(String str) {
        return indexOf(str.toCharArray(), str.length());
    }

    /**
     * <p>
     * 在uri[]中查找字符c的出现位置
     * </p>
     *
     * @param c
     * @param start
     * @return int
     */
    public int indexOf(char c, int start) {
        for (int i = start; i < uriEnd; i++) {
            if (uri[i] == c) {
                return i;
            }
        }
        return -1;
    }
}
