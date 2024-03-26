package com.mini.core;

import com.mini.*;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>
 * StandardPipeline
 * </p>
 *
 * @author Ant
 * @since 2024/3/15 14:04
 */
public class StandardPipeline implements Pipeline {

    protected Valve basic = null;
    protected Container container = null;
    protected int debug = 0;
    protected String info ="com.mini.core.StandardPipeline/0.1";
    // 一组value，可以逐个调用
    protected Valve valves[] = new Valve[0];

    public StandardPipeline() {
        this(null);
    }

    public StandardPipeline(Container container) {
        super();
        setContainer(container);
    }

    public String getInfo() {
        return this.info;
    }

    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public Valve getBasic() {
        return this.basic;
    }

    public void setBasic(Valve valve) {
        // Change components if necessary
        Valve oldBasic = this.basic;
        if (oldBasic == valve) {
            return;
        }
        if (null == valve) {
            return;
        }

        valve.setContainer(container);
        this.basic = valve;
    }

    public void addValve(Valve valve) {
        // Add this Value to the set associated with this Pipeline
        synchronized (valves) {
            Valve results[] = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            results[valves.length] = valve;
            valves = results;
        }
    }

    public Valve[] getValves() {
        if (null == basic) {
            return valves;
        }
        synchronized (valves) {
            Valve results[] = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            results[valves.length] = basic;
            return results;
        }
    }

    public void invoke(Request request, Response response) throws ServletException, IOException {
        System.out.println("StandardPipeline invoke()");
        // 转而调用 context 中的invoke，发起职责链调用
        new StandardPipelineValueContext().invokeNext(request, response);
    }

    public void removeValve(Valve valve) {
        synchronized (valves) {
            // Locate this Value in our list
            int j = -1;
            for (int i = 0; i < valves.length; i++) {
                if (valve == valves[i]) {
                    j = i;
                    break;
                }
            }
            if (j < 0) {
                return;
            }

            valve.setContainer(null);
            // Remove this value from our list
            Valve results[] = new Valve[valves.length -1];
            int n = 0;
            for (int i = 0; i < valves.length; i++) {
                if (i == j) {
                    continue;
                }
                results[n++] = valves[i];
            }
            valves = results;
        }
    }

    protected class StandardPipelineValueContext implements ValveContext {

        protected int stage = 0;

        @Override
        public String getInfo() {
            return null;
        }

        @Override
        public void invokeNext(Request request, Response response) throws IOException, ServletException {
            System.out.println("StandardPipelineValveContext invokeNext()");
            int subscript = stage;
            stage  = stage + 1;

            // Invoke the requested Valve for the current request thread
            if (subscript < valves.length) {
                valves[subscript].invoke(request, response, this);
            } else if ((subscript == valves.length) && (basic != null)) {
                basic.invoke(request, response, this);
            } else {
                throw new ServletException("standardPipeline.noValve");
            }
        }
    }

}
