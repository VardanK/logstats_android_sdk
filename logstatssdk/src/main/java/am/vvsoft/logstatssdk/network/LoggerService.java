package am.vvsoft.logstatssdk.network;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public interface LoggerService {
    @FormUrlEncoded
    @POST("api")
    Observable<Void> sendLog(
            @Field(value = "project", encoded = true) String token,
            @Field(value = "messages") String entries
    );
}
