package server;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/1/4 17:37
 */
public class HttpResponse implements HttpServletResponse {
    private HttpRequest request;

    private OutputStream output;

    private PrintWriter writer;

    String contentType = null;

    long contentLength = -1;
    String charset = null;
    String characterEncoding = null;
    String protocol = "HTTP/1.1";

    // 默认返回成功
    String message = getStatusMessage(HttpServletResponse.SC_OK);
    int status = HttpServletResponse.SC_OK;

    // 保存头信息的map
    Map<String, String> headers = new ConcurrentHashMap<>();

    protected String getStatusMessage(int status) {
        switch (status) {
            case SC_OK:
                return "OK";
            case SC_ACCEPTED:
                return "Accepted";
            case SC_BAD_GATEWAY:
                return "Bad Gateway";
            case SC_BAD_REQUEST:
                return "Bad Request";
            case SC_CONTINUE:
                return "Continue";
            case SC_FORBIDDEN:
                return "Forbidden";
            case SC_INTERNAL_SERVER_ERROR:
                return "Internal Server Error";
            case SC_METHOD_NOT_ALLOWED:
                return "Method Not Allowed";
            case SC_NOT_FOUND:
                return "Not Found";
            case SC_NOT_IMPLEMENTED:
                return ("Not Implemented");
            case SC_REQUEST_URI_TOO_LONG:
                return ("Request URI Too Long");
            case SC_SERVICE_UNAVAILABLE:
                return ("Service Unavailable");
            case SC_UNAUTHORIZED:
                return ("Unauthorized");
            default:
                return "HTTP Response Status" + status;
        }
    }

    public HttpResponse(OutputStream output) {
        this.output = output;
    }

    public HttpRequest getRequest() {
        return request;
    }

    private String getProtocol() {
        return this.protocol;
    }


    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public OutputStream getOutput() {
        return this.output;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
        if (name.toLowerCase() == DefaultHeaders.CONTENT_LENGTH_NAME) {
            setContentLength(Integer.parseInt(value));
        }
        if (name.toLowerCase() == DefaultHeaders.CONNECTION_NAME) {
            setContentType(value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
        if (name.toLowerCase() == DefaultHeaders.CONTENT_LENGTH_NAME) {
            setContentLength(Integer.parseInt(value));
        }
        if (name.toLowerCase() == DefaultHeaders.CONNECTION_NAME) {
            setContentType(value);
        }
    }

    public void sendHeaders() throws IOException {
        PrintWriter outputWrite = getWriter();

        // 状态行输出
        outputWrite.print(this.getProtocol());
        outputWrite.print(" ");
        outputWrite.print(status);
        if (null != message) {
            outputWrite.print(" ");
            outputWrite.print(message);
        }
        outputWrite.print("\r\n");

        if (null != getContentType()) {
            outputWrite.print("Content-Type: "+ getContentType() + "\r\n");
        }

        if (getContentLength() > 0) {
            outputWrite.print("Content-Length: "+ getContentLength() + "\r\n");
        }

        // 输出头信息
        Iterator<String> names = headers.keySet().iterator();
        while (names.hasNext()) {
            String name = names.next();
            String value = headers.get(name);
            outputWrite.print(name + ": " + value + "\r\n");
        }

        // 最后输出空行
        outputWrite.print("\r\n");
        outputWrite.flush();
    }

    @Override
    public void setIntHeader(String name, int value) {

    }

    @Override
    public void addIntHeader(String name, int value) {

    }

    @Override
    public void setStatus(int sc) {
        this.status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.status = sc;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headers.values();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return this.charset;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (null == this.writer) {
            writer = new PrintWriter(new OutputStreamWriter(output, getCharacterEncoding()), true);
        }
        return this.writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public void setContentLength(int len) {
        this.contentLength = len;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    @Override
    public void setContentLengthLong(long len) {

    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
