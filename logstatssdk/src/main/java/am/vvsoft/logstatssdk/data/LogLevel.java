package am.vvsoft.logstatssdk.data;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public enum LogLevel {
    INFO("info"),
    DEBUG("debug"),
    WARNING("warning"),
    ERROR("error"),
    CRITICAL("critical");

    private final String value;

    LogLevel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
