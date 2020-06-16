package com.akame.commonlib.image.loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @Author: Restring
 * @Date: 2018/8/10
 * @Description:
 */
public class GlideLoader implements ILoader {
    @Override
    public void loadImage(ImageConfig imageConfig, ImageView imageView) {
        DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
        Glide.with(imageView.getContext())
                .load(imageConfig.getImagePath())
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory)) //设置渐入渐出动画
                .apply(configOptions(imageConfig))
                .into(imageView);
    }


    @Override
    public void loadImage(ImageConfig imageConfig, ImageView imageView, ImageCallBack imageCallBack) {
        Glide.with(imageView.getContext())
                .load(imageConfig.getImagePath())
                .apply(configOptions(imageConfig))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageCallBack.loadError();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageCallBack.loadSuccess();
                        return false;
                    }
                }).into(imageView);
    }

    @Override
    public void requestPause(Context context) {
        Glide.with(context).pauseRequests();
    }

    @Override
    public void requestResume(Context context) {
        Glide.with(context).resumeRequests();
    }

    @Override
    public void clearMemoryCache(View view) {
        Glide.get(view.getContext()).clearDiskCache();
    }

    @Override
    public void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    private RequestOptions configOptions(ImageConfig imageConfig) {
        RequestOptions options = new RequestOptions();
        List<Transformation<android.graphics.Bitmap>> transList = new ArrayList<>();
        if (imageConfig.isCenterCrop()) {
            transList.add(new CenterCrop());
        }
        if (imageConfig.isCircleCrop()) {
            transList.add(new CircleCrop());
        }
        if (imageConfig.getRounded() != 0) {
            //添加圆角
            transList.add(new RoundedCornersTransformation(imageConfig.getRounded(), 0, imageConfig.getCornerType()));
        }
        if (imageConfig.getBlur() != 0) {
            //高斯模糊
            transList.add(new BlurTransformation(imageConfig.getBlur(), imageConfig.getSampling()));
        }
        if (imageConfig.getBorderWidth() != 0) {
            //圆角边框
            transList.add(new GlideCircleTransform(imageConfig.getContext(), imageConfig.getBorderWidth(), imageConfig.getBorderColor(), imageConfig.getBorderRound()));
        }
        if (transList.size() > 0) {
            MultiTransformation<android.graphics.Bitmap> multi = new MultiTransformation<>(transList);
            options = RequestOptions.bitmapTransform(multi);
        }
        if (imageConfig.getImageWidth() != 0 && imageConfig.getImageHeight() != 0) {
            options = options.override(imageConfig.getImageWidth(), imageConfig.getImageHeight());
        }
        return options
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(imageConfig.getPlaceholderRes());
//                .error(imageConfig.getErrorRes());
    }
}
