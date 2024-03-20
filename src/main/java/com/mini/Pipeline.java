package com.mini;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>
 * 管道接口
 * </p>
 *
 * @author Ant
 * @since 2024/3/13 8:57
 */
public interface Pipeline {
    public Valve getBasic();

    public void setBasic(Valve valve);

    public void addValve(Valve valve);

    public Valve[] getValves();

    public void invoke(Request request, Response response) throws IOException, ServletException;

    public void removeValve(Valve valve);
}
