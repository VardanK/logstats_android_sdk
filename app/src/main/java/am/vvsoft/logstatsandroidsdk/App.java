package am.vvsoft.logstatsandroidsdk;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
