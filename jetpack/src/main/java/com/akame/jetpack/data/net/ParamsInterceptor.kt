package com.akame.jetpack.data.net

import com.akame.developkit.net.BaseParamsInterceptor
import com.akame.jetpack.data.RepositoryManger
import okhttp3.HttpUrl

class ParamsInterceptor : BaseParamsInterceptor() {
    override fun addParams(builder: HttpUrl.Builder) {
        builder.addQueryParameter("111", RepositoryManger.shareManger.getToken())
    }
}