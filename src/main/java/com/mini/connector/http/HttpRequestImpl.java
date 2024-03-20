package com.mini.connector.http;

import com.mini.*;
import com.mini.core.StandardContext;
import com.mini.session.SessionFacade;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/1/4 17:36
 */
public class HttpRequestImpl implements HttpServletRequest, Request {

    private InputStream input;

    private SocketInputStream sis;

    private String uri;

    private boolean parsed = false;

    /**
     * 请求参数字符串
     */
    private String queryString;

    InetAddress address;
    int port;
    protected HashMap<String, String> headers = new HashMap<>();
    protected Map<String, String[]> parameters = new ConcurrentHashMap<>();
    HttpRequestLine requestLine = new HttpRequestLine();

    Cookie[] cookies;
    HttpSession session;
    String sessionId;
    SessionFacade sessionFacade;

    private HttpResponseImpl response;

    public HttpRequestImpl() {}

    public HttpRequestImpl(InputStream input) {
        this.input = input;
        sis = new SocketInputStream(this.input, 2048);
    }

    public void setStream(InputStream input) {
        this.input = input;
        this.sis = new SocketInputStream(this.input, 2048);
    }

    public void setResponse(HttpResponseImpl response) {
        this.response = response;
    }

    /**
     * <p>
     * 应用层协议解析
     * </p>
     *
     * @param socket
     * @return void
     */
    public void parse(Socket socket) throws IOException {
        try {
            // 解析连接信息
            parseConnection(socket);

            this.sis.readRequestLine(requestLine);

            parseRequestLine();

            parseHeaders();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        this.uri = new String(requestLine.uri, 0, requestLine.uriEnd);
    }

    /**
     * <p>
     * 解析连接信息
     * </p>
     *
     * @param socket
     * @return void
     */
    private void parseConnection(Socket socket) {
        this.address = socket.getInetAddress();
        this.port = socket.getPort();
    }

    /**
     * <p>
     * 解析请求行
     * </p>
     *
     * @return void
     */
    private void parseRequestLine() {
        int question = requestLine.indexOf("?");
        if (question > 0) {
            this.queryString = new String(requestLine.uri, question + 1, requestLine.uriEnd - question - 1);
            this.uri = new String(requestLine.uri, 0 , question);
        } else {
            queryString = null;
            uri = new String(requestLine.uri, 0, requestLine.uriEnd);
        }

        // 处理参数串中带有jsessionid的情况
        String tmp = ";" + DefaultHeaders.JSESSIONID_NAME + "=";
        int semicolon = uri.indexOf(tmp);
        if (semicolon >= 0) {
            sessionId = uri.substring(semicolon + tmp.length());
            uri = uri.substring(0, semicolon);
        }
    }

    /**
     * <p>
     * 解析头信息
     * </p>
     *
     * @return void
     */
    private void parseHeaders() throws IOException, ServletException {
        while (true) {
            HttpHeader header = new HttpHeader();
            sis.readHeader(header);
            if (header.nameEnd == 0) {
                if (header.valueEnd == 0) {
                    return;
                } else {
                    throw new ServletException("httpProcessor.parseHeaders.colon");
                }
            }
            String name = new String(header.name, 0, header.nameEnd);
            String value = new String(header.value, 0, header.valueEnd);
            name = name.toLowerCase();
            // Set the corresponding request headers
            if (name.equals(DefaultHeaders.ACCEPT_LANGUAGE_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.CONTENT_LENGTH_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.CONTENT_TYPE_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.HOST_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.CONNECTION_NAME)) {
                headers.put(name, value);
                if (value.equals("close")) {
                    response.setHeader("Connection", "close");
                }
            } else if (name.equals(DefaultHeaders.TRANSFER_ENCODING_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.COOKIE_NAME)) {
                headers.put(name, value);
                Cookie[] cookieArr = parseCookieHeader(value);
                this.cookies = cookieArr;
                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equals("jsessionid")) {
                        this.sessionId = cookies[i].getValue();
                    }
                }
            } else {
                headers.put(name, value);
            }
        }
    }

    public  Cookie[] parseCookieHeader(String header) {
        if ((header == null) || (header.length() < 1) ) {
            return (new Cookie[0]);
        }
        ArrayList<Cookie> cookieal = new ArrayList<>();
        while (header.length() > 0) {
            int semicolon = header.indexOf(';');
            if (semicolon < 0) {
                semicolon = header.length();
            }
            if (semicolon == 0) {
                break;
            }

            String token = header.substring(0, semicolon);
            if (semicolon < header.length()) {
                header = header.substring(semicolon + 1);
            } else {
                header = "";
            }

            try {
                int equals = token.indexOf('=');
                if (equals > 0) {
                    String name = token.substring(0, equals).trim();
                    String value = token.substring(equals+1).trim();
                    cookieal.add(new Cookie(name, value));
                }
            } catch (Throwable e) {
            }
        }
        return ((Cookie[]) cookieal.toArray (new Cookie [cookieal.size()]));
    }

    protected void parseParameters() {
        String encoding = getCharacterEncoding();
        System.out.println(encoding);
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        String qString = getQueryString();
        System.out.println("getQueryString:"+qString);
        if (qString != null) {
            byte[] bytes = new byte[qString.length()];
            try {
                bytes=qString.getBytes(encoding);
                parseParameters(this.parameters, bytes, encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();;
            }
        }
        String contentType = getContentType();
        if (contentType == null) {
            contentType = "";
        }
        int semicolon = contentType.indexOf(';');
        if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
        }
        else {
            contentType = contentType.trim();
        }
        if ("POST".equals(getMethod()) && (getContentLength() > 0)
                && "application/x-www-form-urlencoded".equals(contentType)) {
            try {
                int max = getContentLength();
                int len = 0;
                byte buf[] = new byte[getContentLength()];
                ServletInputStream is = getInputStream();
                while (len < max) {
                    int next = is.read(buf, len, max - len);
                    if (next < 0) {
                        break;
                    }
                    len += next;
                }
                is.close();
                if (len < max) {
                    throw new RuntimeException("Content length mismatch");
                }
                parseParameters(this.parameters, buf, encoding);
            }
            catch (UnsupportedEncodingException ue) {
            }
            catch (IOException e) {
                throw new RuntimeException("Content read fail");
            }
        }
    }

    private byte convertHexDigit(byte b) {
        if ((b >= '0') && (b <= '9')) {
            return (byte)(b - '0');
        }
        if ((b >= 'a') && (b <= 'f')) {
            return (byte)(b - 'a' + 10);
        }
        if ((b >= 'A') && (b <= 'F')) {
            return (byte)(b - 'A' + 10);
        }
        return 0;
    }

    public void parseParameters(Map<String,String[]> map, byte[] data, String encoding)
            throws UnsupportedEncodingException {
        if (parsed) {
            return;
        }
        System.out.println(data);
        if (data != null && data.length > 0) {
            int    pos = 0;
            int    ix = 0;
            int    ox = 0;
            String key = null;
            String value = null;
            while (ix < data.length) {
                byte c = data[ix++];
                switch ((char) c) {
                    case '&':
                        value = new String(data, 0, ox, encoding);
                        if (key != null) {
                            putMapEntry(map,key, value);
                            key = null;
                        }
                        ox = 0;
                        break;
                    case '=':
                        key = new String(data, 0, ox, encoding);
                        ox = 0;
                        break;
                    case '+':
                        data[ox++] = (byte)' ';
                        break;
                    case '%':
                        data[ox++] = (byte)((convertHexDigit(data[ix++]) << 4)
                                + convertHexDigit(data[ix++]));
                        break;
                    default:
                        data[ox++] = c;
                }
            }
            //The last value does not end in '&'.  So save it now.
            if (key != null) {
                value = new String(data, 0, ox, encoding);
                putMapEntry(map,key, value);
            }
        }
        parsed = true;
    }

    private static void putMapEntry( Map<String,String[]> map, String name, String value) {
        String[] newValues = null;
        String[] oldValues = (String[]) map.get(name);
        if (oldValues == null) {
            newValues = new String[1];
            newValues[0] = value;
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getMethod() {
        return new String(this.requestLine.method, 0, this.requestLine.methodEnd);
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (sessionFacade != null) {
            return sessionFacade;
        }
        if (null != sessionId) {
            session = HttpConnector.sessions.get(sessionId);
            if (session != null) {
                sessionFacade = new SessionFacade(session);
                return sessionFacade;
            } else {
                session = HttpConnector.createSession();
                sessionFacade = new SessionFacade(session);
                return sessionFacade;
            }
        } else {
            session = HttpConnector.createSession();
            sessionFacade = new SessionFacade(session);
            sessionId = session.getId();

            return sessionFacade;
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public HttpSession getSession() {
        return this.sessionFacade;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        parseParameters();
        String values[] = parameters.get(name);
        if (values != null) {
            return (values[0]);
        } else {
            return (null);
        }
    }

    @Override
    public Enumeration<String> getParameterNames() {
        parseParameters();
        return (Collections.enumeration(parameters.keySet()));
    }

    @Override
    public String[] getParameterValues(String name) {
        parseParameters();
        String values[] = parameters.get(name);
        if (values != null) {
            return (values);
        } else {
            return (null);
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        parseParameters();
        return (this.parameters);
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
    public Connector getConnector() {
        return null;
    }
    @Override
    public void setConnector(Connector connector) {
    }
    @Override
    public Context getContext() {
        return null;
    }
    @Override
    public void setContext(Context context) {
    }
    @Override
    public String getInfo() {
        return null;
    }
    @Override
    public ServletRequest getRequest() {
        return null;
    }
    @Override
    public Response getResponse() {
        return null;
    }
    @Override
    public void setResponse(Response response) {
    }
    @Override
    public Socket getSocket() {
        return null;
    }
    @Override
    public void setSocket(Socket socket) {
    }
    @Override
    public InputStream getStream() {
        return null;
    }
    @Override
    public Wrapper getWrapper() {
        return null;
    }
    @Override
    public void setWrapper(Wrapper wrapper) {
    }
    @Override
    public ServletInputStream createInputStream() throws IOException {
        return null;
    }
    @Override
    public void finishRequest() throws IOException {
    }
    @Override
    public void recycle() {
    }
    @Override
    public void setContentLength(int length) {
    }
    @Override
    public void setContentType(String type) {
    }
    @Override
    public void setProtocol(String protocol) {
    }
    @Override
    public void setRemoteAddr(String remote) {
    }
    @Override
    public void setScheme(String scheme) {
    }
    @Override
    public void setServerPort(int port) {
    }
}
