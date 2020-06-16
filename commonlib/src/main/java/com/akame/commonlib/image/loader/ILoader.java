package com.akame.commonlib.image.loader;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * @Author: Restring
 * @Date: 2018/8/10
 * @Description:
 */
public interface ILoader {
    /* 加载ImageView**/
    void loadImage(ImageConfig imageConfig, ImageView imageView);

    /* 加载ImageView含监听**/
    void loadImage(ImageConfig imageConfig, ImageView imageView, ImageCallBack imageCallBack);

    /* 暂停加载**/
    void requestPause(Context context);

    /* 重新加载**/
    void requestResume(Context context);

    /* 清除某个ImageView的磁盘缓存**/
    void clearMemoryCache(View view);

    /*清除所有缓存**/
    void clearMemory(Context context);
}
