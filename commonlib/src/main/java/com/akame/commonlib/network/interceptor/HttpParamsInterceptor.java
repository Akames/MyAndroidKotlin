package com.akame.commonlib.network.interceptor;


import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author: Restring
 * @Date: 2018/9/25
 * @Description 参数拦截器:
 */

public class HttpParamsInterceptor implements Interceptor {
    private Map<String, String> paramsMap;

    public void setParamsMap(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //添加公有参数
        request = addPostParams(request);
        return chain.proceed(request);
    }

    //post 添加签名和公共参数
    private Request addPostParams(Request request) {
        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = request.url()
                .newBuilder()
                .scheme(request.url().scheme())
                .host(request.url().host());
        // 添加全局参数
        //遍历paramsMap
        if (paramsMap != null) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                authorizedUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        // 新的请求
        return request.newBuilder()
                .method(request.method(), request.body())
                .url(authorizedUrlBuilder.build())
                .build();
    }
}
