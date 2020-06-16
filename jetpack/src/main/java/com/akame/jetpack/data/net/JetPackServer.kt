package com.akame.jetpack.data.net

import com.akame.developkit.net.BaseRetrofitClient
import com.akame.developkit.net.CacheInterceptor
import com.akame.jetpack.JetPackApp
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

class JetPackServer : BaseRetrofitClient() {

    override fun configOkHttpBuilder(builder: OkHttpClient.Builder) {
        val httpCacheDirectory = File(JetPackApp.app.cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024L // 10 MiB
        val cache = Cache(httpCacheDirectory, cacheSize)
        builder
            .cache(cache)
            //添加缓存
            .addInterceptor(CacheInterceptor(JetPackApp.app))
            //添加公共参数
            .addInterceptor(ParamsInterceptor())
    }

}
