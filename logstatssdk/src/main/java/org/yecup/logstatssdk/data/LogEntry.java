package org.yecup.logstatssdk.data;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public class LogEntry {
    public final String level;
    public final String message;
    public final long time;
    public final LogContext context;

    public LogEntry(LogLevel level, String message) {
        this.level = level.toString();
        this.message = message;
        time = System.currentTimeMillis() / 1000;
        context = LogContext.getInstance();
    }
}
