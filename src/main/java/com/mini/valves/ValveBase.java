package com.mini.valves;

import com.mini.*;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author GaoXin
 * @since 2024/3/15 13:51
 */
public abstract class ValveBase implements Valve {

    protected Container container = null;

    protected int debug = 0;

    protected static String info = "com.mini.valves.ValueBase/1.0";

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {

    }

    public int getDebug() {
        return this.debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }
}
