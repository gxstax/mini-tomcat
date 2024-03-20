package com.mini;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/12 18:34
 */
public interface Connector {
    public Container getContainer();

    public void setContainer(Container container);

    public String getInfo();

    public String getScheme();

    public void setScheme(String scheme);

    public Request createRequest();

    public Response createResponse();

    public void initialize();
}
