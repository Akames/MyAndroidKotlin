package com.akame.commonlib.network;

import com.akame.commonlib.utils.GsonUtil;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @Author: Akame
 * @Date: 2019/2/22
 * @Description: requestBody转化器  可以将参数 转化成requestBody上传到服务器
 */
public class RequestBodyManage {

    /**
     * 文本转化
     *
     * @param param 文本参数
     * @return requestBody
     */
    public static RequestBody toFormatText(String param) {
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }


    /**
     * 图片转化
     *
     * @param imageFile 图片文件
     * @return requestBody
     */
    public static RequestBody toFormatImg(File imageFile) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
    }

    /**
     * 音频转化
     *
     * @param autoFile 音频文件
     * @return requestBody
     */
    public static RequestBody toFormatAuto(File autoFile) {
        return RequestBody.create(MediaType.parse("audio/*"), autoFile);
    }

    /**
     * 将requestBody 转化成 Part 上传 比如图片就需再次转化
     *
     * @param requestBody 请求文件体
     * @return part
     */
    public static MultipartBody.Part requestBodyToPart(String key, String fileName, RequestBody requestBody) {
        return MultipartBody.Part.createFormData(key, fileName, requestBody);
    }

    /**
     * 将map参数转化为json串传输
     *
     * @param map 参数
     * @return RequestBody
     */
    public static RequestBody conversion(Map<String, Object> map) {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), GsonUtil.toJson(map));
    }

}
