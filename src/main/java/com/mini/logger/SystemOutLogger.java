package com.mini.logger;

/**
 * <p>
 * 标准输出日志
 * </p>
 *
 * @author Ant
 * @since 2024/3/13 19:17
 */
public class SystemOutLogger {
    protected static final String info = "com.mini.logger.SystemOutLogger/1.0";

    public void log(String msg) {
        System.out.println(msg);
    }
}
