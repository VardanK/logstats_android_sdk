package am.vvsoft.logstatssdk;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public interface LogStatsLoggerInterface {
    void d(String message);

    void i(String message);

    void w(String message);

    void e(String message);

    void f(String message);
}
