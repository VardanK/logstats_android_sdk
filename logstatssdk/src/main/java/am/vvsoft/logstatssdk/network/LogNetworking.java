package am.vvsoft.logstatssdk.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import am.vvsoft.logstatssdk.data.LogEntry;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public class LogNetworking {
    private final LoggerService loggerService;
    private final String writeToken;

    public LogNetworking(String baseUrl, String writeToken){

        Gson gson = (new GsonBuilder())
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.loggerService = retrofit.create(LoggerService.class);

        this.writeToken = writeToken;
    }

    public Observable<Void> sendData(List<LogEntry> entries){
        return loggerService.sendLog(
                writeToken,
                (new Gson()).toJson(entries));
    }
}
