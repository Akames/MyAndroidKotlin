package com.akame.jetpack.data

import com.akame.jetpack.data.net.JetPackServer
import com.akame.jetpack.data.net.ServiceApi

class RepositoryManger {
    companion object {
        val service by lazy {
            JetPackServer().getRetrofit(ServiceApi::class.java, ServiceApi.BASE_URL)
        }

        val shareManger by lazy { ShareManger() }
    }
}