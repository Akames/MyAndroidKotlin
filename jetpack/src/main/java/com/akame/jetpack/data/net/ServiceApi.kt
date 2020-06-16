package com.akame.jetpack.data.net

import com.akame.jetpack.BuildConfig
import com.akame.jetpack.data.net.been.GirlBeen
import com.akame.jetpack.data.net.been.ImageViewBeen
import com.akame.jetpack.data.net.been.ServerResult
import retrofit2.http.GET
import retrofit2.http.Path


interface ServiceApi {
    companion object {
        const val BASE_URL = BuildConfig.SERVER_URL
    }

    @GET("data/福利/10/1")
//     fun getImageListAsync(): Single<ServerResult<ObservableArrayList<ImageViewBeen>>>
    suspend fun getImageListAsync(): ServerResult<ArrayList<ImageViewBeen>>

    @GET("data/category/Girl/type/Girl/page/{page}/count/10")
    suspend fun getGirlList(@Path("page") page: Int): ServerResult<ArrayList<GirlBeen>>

}