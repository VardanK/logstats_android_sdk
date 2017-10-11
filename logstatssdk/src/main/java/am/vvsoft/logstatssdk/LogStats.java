package am.vvsoft.logstatssdk;

import android.util.Log;

import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import am.vvsoft.logstatssdk.data.LogEntry;
import am.vvsoft.logstatssdk.data.LogLevel;
import am.vvsoft.logstatssdk.data.LogStorage;
import am.vvsoft.logstatssdk.network.LogNetworking;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


public class LogStats implements LogStatsLoggerInterface {

    private final LogStorage logStorage;
    private final LogNetworking logNetworking;
    private final int LOG_SEND_TICK_INTERVAL = 1;

    private Disposable poolDisposable = null;

    public LogStats(String baseUrl, String token) {
        this.logStorage = new LogStorage();
        this.logNetworking = new LogNetworking(baseUrl, token);

        startLoggerPool();
    }

    public void destroy(){
        stopLoggerPool();
    }

    private void startLoggerPool(){
        if(poolDisposable == null) {
            poolDisposable = Observable.interval(LOG_SEND_TICK_INTERVAL, TimeUnit.SECONDS)
                    .filter(
                            i -> {
                                long index = i + 1;
                                boolean sendData = false;
                                if(logStorage.isPendingRetry()) {
                                    if(index % 10 == 0) {
                                        logStorage.resetRetryFlag();
                                        sendData = true;
                                    }
                                } else if(!logStorage.hasPending()) {
                                    if(logStorage.bufferReady() || (index % 10 == 0 && !logStorage.bufferEmpty()))
                                    {
                                        sendData = true;
                                    }
                                }

                                return sendData;
                            }
                    )
                    .flatMap(index -> logNetworking.sendData(logStorage.createPendingList()))
                    .retry(throwable -> {
                        if(throwable instanceof HttpException){
                            HttpException exception = (HttpException)throwable;
                            if(exception.code() >= 500 && exception.code() < 600){
                                // It looks like the server is experiencing some issues, retry later
                                logStorage.retryPending();
                                return true;
                            }
                        } else if(throwable instanceof ConnectException) {
                            logStorage.retryPending();
                            return true;
                        }

                        return false;
                    })
                    .subscribe(aVoid -> {
                        logStorage.resetPending();
                    }, throwable -> {
                        logStorage.setDisabled();
                        Log.e("LogStats", throwable.getMessage());
                    });
        }
    }

    private void stopLoggerPool(){
        if(poolDisposable != null){
            if(!poolDisposable.isDisposed()) {
                poolDisposable.dispose();
            }

            poolDisposable = null;
        }
    }

    @Override
    public void d(String message) {
        logStorage.addEntry(
                new LogEntry(LogLevel.DEBUG, message)
        );
    }

    @Override
    public void i(String message) {
        logStorage.addEntry(
                new LogEntry(LogLevel.INFO, message)
        );
    }

    @Override
    public void w(String message) {
        logStorage.addEntry(
                new LogEntry(LogLevel.WARNING, message)
        );
    }

    @Override
    public void e(String message) {
        logStorage.addEntry(
                new LogEntry(LogLevel.ERROR, message)
        );
    }

    @Override
    public void f(String message) {
        logStorage.addEntry(
                new LogEntry(LogLevel.CRITICAL, message)
        );
    }
}
