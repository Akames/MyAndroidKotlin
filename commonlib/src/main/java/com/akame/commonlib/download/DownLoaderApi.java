package com.akame.commonlib.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @Author: Akame
 * @Date: 2018/12/20
 * @Description: 配置下载地址
 */
public interface DownLoaderApi {
    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
