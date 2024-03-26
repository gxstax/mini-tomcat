package test;

import javax.servlet.*;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/26 10:50
 */
public class TestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("The very first Filter");
        chain.doFilter(request, response);
    }
}
