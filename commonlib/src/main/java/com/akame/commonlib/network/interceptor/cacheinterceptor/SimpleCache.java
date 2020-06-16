package com.akame.commonlib.network.interceptor.cacheinterceptor;

/**
 * 缓存接口
 * Created by junhua on 2018/3/18.
 */
public interface SimpleCache {
    void put(String key, String value);

    String get(String key);

}
