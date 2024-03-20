package com.mini.logger;

import com.mini.Logger;

import javax.servlet.ServletException;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * <p>
 * 基础日志
 * </p>
 *
 * @author Ant
 * @since 2024/3/13 9:14
 */
public abstract class LoggerBase implements Logger {
    protected int debug = 0;
    protected  static final String info = "com.mini.logger.looggerBase/1.0";
    protected int verbosity = ERROR;

    public int getDebug() {
        return this.debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public void setVerbosityLevel(String verbosity) {
        if ("FATAL".equalsIgnoreCase(verbosity)) {
            this.verbosity = FATAL;
        } else if ("ERROR".equalsIgnoreCase(verbosity)) {
            this.verbosity = ERROR;
        } else if ("WARNING".equalsIgnoreCase(verbosity)) {
            this.verbosity = WARNING;
        } else if ("INFORMATION".equalsIgnoreCase(verbosity)) {
            this.verbosity = INFORMATION;
        } else if ("DEBUG".equalsIgnoreCase(verbosity)) {
            this.verbosity = DEBUG;
        }
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public int getVerbosity() {
        return this.verbosity;
    }

    @Override
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * <p>
     * 由具体的业务来实现
     * </p>
     *
     * @param msg
     * @return void
     */
    public abstract void log(String msg);


    @Override
    public void log(Exception exception, String msg) {
        log(msg, exception);
    }

    /**
     * <p>
     * 核心方法
     * </p>
     *
     * @param msg
     * @param throwable
     * @return void
     */
    @Override
    public void log(String msg, Throwable throwable) {
        CharArrayWriter buf = new CharArrayWriter();
        PrintWriter writer = new PrintWriter(buf);
        writer.print(msg);
        throwable.printStackTrace(writer);

        Throwable rootCause = null;
        if (throwable instanceof ServletException) {
            rootCause = ((ServletException) throwable).getRootCause();
        }
        if (null != rootCause) {
            writer.println("---- Root Cause ----");
            rootCause.printStackTrace(writer);
        }
        log(buf.toString());
    }

    @Override
    public void log(String message, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(message);
        }
    }

    @Override
    public void log(String message, Throwable throwable, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(message, throwable);
        }
    }
}
