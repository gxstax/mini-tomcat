package com.mini;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/12 18:35
 */
public interface Response {
    public Connector getConnector();

    public void setConnector(Connector connector);

    public int getContentCount();

    public Context getContext();

    public void setContext(Context context);

    public String getInfo();

    public Request getRequest();

    public void setRequest(Request request);

    public ServletResponse getResponse();

    public OutputStream getStream();

    public void setStream(OutputStream stream);

    public void setError();

    public boolean isError();

    public ServletOutputStream createOutputStream() throws IOException;

    public void finishResponse() throws IOException;

    public int getContentLength();

    public String getContentType();

    public PrintWriter getReporter();

    public void recycle();

    public void resetBuffer();

    public void sendAcknowledgement() throws IOException;
}
