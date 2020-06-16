package com.akame.commonlib.network;

import com.akame.commonlib.BuildConfig;
import com.akame.commonlib.CommonLib;
import com.akame.commonlib.network.interceptor.cacheinterceptor.SimpleCacheInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerManage {
    private static ServerManage serverManage;
    private Retrofit retrofit;
    private HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private Interceptor paramsInterceptor;

    private ServerManage() {
    }

    public static ServerManage getInstance() {
        synchronized (ServerManage.class) {
            if (serverManage == null) {
                serverManage = new ServerManage();
            }
            return serverManage;
        }
    }

    public ServerManage setParamsInterceptor(Interceptor paramsInterceptor) {
        this.paramsInterceptor = paramsInterceptor;
        return this;
    }

    public synchronized Retrofit getRetrofitServer(String baseUrl) {
        if (retrofit == null) {
            retrofit = getRetrofit(getOkHttpClient(), baseUrl);
        }
        return retrofit;
    }

    private Retrofit getRetrofit(OkHttpClient client, String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create()) 将返回数据转化成String
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        //设置缓存
//        File cacheFile = new File(LibConstants.PATH_CACHE);
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
//        CacheInterceptor interceptor = new CacheInterceptor();
//        builder.cache(cache);
//        builder.addInterceptor(interceptor);
//        builder.addNetworkInterceptor(interceptor);
        builder.addInterceptor(new SimpleCacheInterceptor(CommonLib.getApplication()));
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        //cookie认证
        builder.cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        });
        //添加公用参数 根据项目需求是否添加
        if (paramsInterceptor != null) {
            builder.addInterceptor(paramsInterceptor);
        }
        return builder.build();
    }

}
