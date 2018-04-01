package wang.relish.accumulation.network;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * @author Relish Wang
 * @since 2018/04/01
 */
public class RetrofitManager {

    private static final StandRespGsonConverterFactory GSON_CONVERT_INSTANCE =
            StandRespGsonConverterFactory.create(
                    new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create());

    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(SingleInstanceUtils.getOkhttpClientInstance())
                    .baseUrl("http://45.77.206.186:3000/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GSON_CONVERT_INSTANCE)
                    .build();
        }
        return retrofit;
    }
}
