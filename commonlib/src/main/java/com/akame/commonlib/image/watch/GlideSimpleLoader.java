package com.akame.commonlib.image.watch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.ielse.imagewatcher.ImageWatcher;

/**
 * @Author: Akame
 * @Date: 2018/12/28
 * @Description: 图片查看
 */
public class GlideSimpleLoader implements ImageWatcher.Loader {

    @Override
    public void load(Context context, Uri uri, ImageWatcher.LoadCallback loadCallback) {
        Glide.with(context).load(uri)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        loadCallback.onResourceReady(resource);
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        loadCallback.onLoadFailed(errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        loadCallback.onLoadStarted(placeholder);
                    }
                });
    }
}
