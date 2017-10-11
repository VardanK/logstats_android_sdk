package am.vvsoft.logstatssdk.data;

import android.os.Build;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public class LogContext {
    public final String deviceModel;

    private static volatile transient LogContext instance = null;

    private LogContext(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public static LogContext getInstance(){
        if(instance == null){
            synchronized (LogContext.class){
                if(instance == null){
                    instance = new LogContext(
                            Build.MANUFACTURER + "_" + Build.PRODUCT
                    );
                }
            }
        }

        return instance;
    }
}
