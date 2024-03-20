package com.mini.core;

import com.mini.Request;
import com.mini.Response;
import com.mini.ValveContext;
import com.mini.connector.HttpRequestFacade;
import com.mini.connector.HttpResponseFacade;
import com.mini.connector.http.HttpRequestImpl;
import com.mini.connector.http.HttpResponseImpl;
import com.mini.valves.ValveBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/18 16:43
 */
public class StandardWrapperValve extends ValveBase {

    @Override
    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {
        // TODO Auto-generated method stub
        System.out.println("StandardWrapperValue invoke()");
        HttpServletRequest requestFacade = new HttpRequestFacade((HttpRequestImpl) request);
        HttpServletResponse responseFacade = new HttpResponseFacade((HttpResponseImpl)response);
        Servlet instance = ((StandardWrapper) getContainer()).getServlet();

        if (null != instance) {
            instance.service(requestFacade, responseFacade);
        }
    }
}
