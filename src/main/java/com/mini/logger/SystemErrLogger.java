package com.mini.logger;

/**
 * <p>
 * 标准错误日志
 * </p>
 *
 * @author Ant
 * @since 2024/3/13 19:16
 */
public class SystemErrLogger extends LoggerBase {
    protected static final String info = "com.mini.logger.SystemErrLogger/0.1";

    public void log(String msg) {
        System.err.println(msg);
    }
}
