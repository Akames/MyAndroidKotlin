package com.akame.commonlib.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.akame.commonlib.image.loader.ImageConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @Author: Restring
 * @Date: 2018/8/13
 * @Description:
 */
public class ImageLoad {
    private static WeakReference<ImageConfig.Builder> builder;

    public static ImageConfig.Builder with(Context context) {
        if (builder == null) {
            synchronized (ImageLoad.class) {
                if (builder == null) {
                    builder = new WeakReference<>(new ImageConfig.Builder());
                }
            }
        }
        return builder.get().context(context);
    }

    public static void pause() {
        if (builder != null) {
            builder.get().pause();
        }
    }

    public static void resume() {
        if (builder != null) {
            builder.get().resume();
        }
    }

    public static void clearMemoryCache(View view) {
        if (builder != null) {
            builder.get().clearMemoryCache(view);
        }
    }

    public static void clearMemory() {
        if (builder != null) {
            builder.get().clearMemory();
        }
    }

    /**
     * Glide 等比例加载图片 这个是以宽为标准 比如加载长图的时候可以使用本方法
     *
     * @param url       图片地址
     * @param imageView 图片控件
     */
    public static void constrainLoad(String url, final ImageView imageView, boolean isHigh) {
        Glide.with(imageView.getContext())
                .load(url)
                .apply(new RequestOptions().placeholder(ImageConfig.getPlaceholder())
                        .priority(isHigh ? Priority.HIGH : Priority.NORMAL))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        float width = resource.getIntrinsicWidth();
                        float height = resource.getIntrinsicHeight();
                        float ivWidth = imageView.getWidth();
                        if (ivWidth == 0) {
                            ivWidth = imageView.getResources().getDisplayMetrics().widthPixels;
                        }
                        int ivHeight = (int) (height / width * ivWidth);
                        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                        lp.height = ivHeight;
                        imageView.setLayoutParams(lp);
                        imageView.setImageDrawable(resource);
                    }
                });
    }

    public static void constrainLoad(String url, final ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .apply(new RequestOptions().placeholder(ImageConfig.getPlaceholder()))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        float width = resource.getIntrinsicWidth();
                        float height = resource.getIntrinsicHeight();
                        float ivWidth = imageView.getWidth();
                        if (ivWidth == 0) {
                            ivWidth = imageView.getResources().getDisplayMetrics().widthPixels;
                        }
                        int ivHeight = (int) (height / width * ivWidth);
                        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                        lp.height = ivHeight;
                        imageView.setLayoutParams(lp);
                        imageView.setImageDrawable(resource);
                    }
                });
    }

    /**
     * 带圆角的等比例加载
     *
     * @param url
     * @param imageView
     * @param fillet
     */
    public static void constrainLoadFillet(String url, final ImageView imageView, int fillet) {
        Glide.with(imageView.getContext())
                .load(url)
                .apply(new RequestOptions().placeholder(ImageConfig.getPlaceholder())
                        .transform(new RoundedCornersTransformation(fillet, 0, RoundedCornersTransformation.CornerType.ALL) ))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        float width = resource.getIntrinsicWidth();
                        float height = resource.getIntrinsicHeight();
                        float ivWidth = imageView.getWidth();
                        if (ivWidth == 0) {
                            ivWidth = imageView.getResources().getDisplayMetrics().widthPixels;
                        }
                        int ivHeight = (int) (height / width * ivWidth);
                        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                        lp.height = ivHeight;
                        imageView.setLayoutParams(lp);
                        imageView.setImageDrawable(resource);
                    }
                });
    }
}
