package wang.relish.accumulation.network;

import com.google.gson.Gson;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * @author doslin
 * @description 单例工具类
 * @since 2016/10/24
 */
public class SingleInstanceUtils {

    private static volatile Gson sGsonInstance;
    private static volatile OkHttpClient sOkhttpClient;
    private static volatile boolean sDebuggable = false;
    private static volatile Interceptor sInterceptor;
    private static volatile CopyOnWriteArrayList<Interceptor> sNetworkInterceptor = new CopyOnWriteArrayList<>();

    private SingleInstanceUtils() {
    }

    public static Gson getGsonInstance() {
        if (sGsonInstance == null) {
            synchronized (SingleInstanceUtils.class) {
                if (sGsonInstance == null) {
                    sGsonInstance = new Gson();
                }
            }
        }
        return sGsonInstance;
    }

    public static OkHttpClient getOkhttpClientInstance() {
        if (sOkhttpClient == null) {
            synchronized (SingleInstanceUtils.class) {
                if (sOkhttpClient == null) {
                    OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
                    if (!sNetworkInterceptor.isEmpty()) {
                        for (Interceptor interceptor : sNetworkInterceptor) {
                            if (interceptor != null) {
                                okHttpBuilder.addNetworkInterceptor(interceptor);
                            }
                        }
                    }
                    if (sInterceptor != null) {
                        okHttpBuilder.addInterceptor(sInterceptor);
                    }
                    sOkhttpClient = okHttpBuilder
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return sOkhttpClient;
    }

    /**
     * 添加拦截器
     *
     * @param interceptor
     */
    public static void setCustomeOkhttpInterceptor(Interceptor interceptor) {
        sInterceptor = interceptor;
        sOkhttpClient = null;
    }

    /**
     * 添加自定义网络拦截器
     *
     * @param interceptor
     */
    public static void setCustomeNetworkInterceptor(Interceptor interceptor) {
        addCustomerNetworkInterceptor(interceptor);
    }

    /**
     * 添加自定义网络拦截器
     *
     * @param interceptor
     */
    public static void addCustomerNetworkInterceptor(Interceptor interceptor) {
        sNetworkInterceptor.add(interceptor);
        sOkhttpClient = null;
    }

}
