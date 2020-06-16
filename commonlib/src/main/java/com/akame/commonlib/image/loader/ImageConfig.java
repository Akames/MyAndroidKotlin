package com.akame.commonlib.image.loader;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.akame.commonlib.R;

import java.util.Random;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @Author: Restring
 * @Date: 2018/8/10
 * @Description:
 */
public class ImageConfig {
    private Builder builder;
    private ILoader iLoader;
    private static ImageConfig instance;

    public static ImageConfig getInstance() {
        synchronized (ImageConfig.class) {
            if (instance == null) {
                instance = new ImageConfig();
            }
            return instance;
        }
    }

    public ImageConfig builder(ImageConfig.Builder builder) {
        this.builder = builder;
        iLoader = new GlideLoader();
        return this;
    }

    public int getPlaceholderRes() {
        return builder.placeholderRes;
    }

    public int getErrorRes() {
        return builder.errorRes;
    }

    public Context getContext() {
        return builder.context;
    }

    public Object getImagePath() {
        return builder.imagePath;
    }

    public int getBlur() {
        return builder.blur;
    }

    public int getRounded() {
        return builder.rounded;
    }

    public RoundedCornersTransformation.CornerType getCornerType() {
        return builder.cornerType;
    }

    public int getBorderWidth() {
        return builder.borderWidth;
    }

    public int getBorderColor() {
        return builder.borderColor;
    }

    public int getBorderRound() {
        return builder.borderRound;
    }

    public int getSampling() {
        return builder.sampling;
    }

    public boolean isCenterCrop() {
        return builder.isCenterCrop;
    }

    public boolean isCircleCrop() {
        return builder.isCircleCrop;
    }

    public int getImageWidth() {
        return builder.imageWidth;
    }

    public int getImageHeight() {
        return builder.imageHeight;
    }

    public void show(ImageView imageView) {
        iLoader.loadImage(this, imageView);
    }

    public void pause() {
        iLoader.requestPause(builder.context);
    }

    public void resume() {
        iLoader.requestResume(builder.context);
    }

    public void imageCallBack(ImageView imageView, ImageCallBack imageCallBack) {
        iLoader.loadImage(this, imageView, imageCallBack);
    }

    public void clearMemoryCache(View view) {
        iLoader.clearMemoryCache(view);
    }

    public void clearMemory() {
        iLoader.clearMemory(builder.context);
    }

    public static class Builder {
        //图片加载中默认图片
        private int placeholderRes;
        //图片加载失败默认图片
        private int errorRes;
        //上下文对象
        private Context context;
        //图片加载地址
        private Object imagePath;
        //模糊度和放大的倍数
        private int blur, sampling;
        //圆角度数
        private int rounded;
        //圆角的状态
        private RoundedCornersTransformation.CornerType cornerType;
        // 边框宽度 边框颜色  边框圆角角度
        private int borderWidth, borderColor, borderRound;
        //是否平铺 是否拉伸加载
        private boolean isCenterCrop, isCircleCrop;

        private int imageWidth, imageHeight;

        private void init() {
            placeholderRes = getPlaceholder();
            errorRes = placeholderRes;
            blur = 0;
            sampling = 1;
            rounded = 0;
            imageWidth = 0;
            imageHeight = 0;
            borderRound = 0;
            borderColor = 0;
            borderWidth = 0;
            isCenterCrop = false;
            isCircleCrop = false;
        }

        public Builder context(Context context) {
            this.context = context;
            init();
            return this;
        }

        public Builder placeHolderRes(int placeholderRes) {
            this.placeholderRes = placeholderRes;
            return this;
        }

        public Builder errorRes(int errorRes) {
            this.errorRes = errorRes;
            return this;
        }

        public Builder url(Object imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder blur(int blur) {
            this.blur = blur;
            return this;
        }

        public Builder blur(int blur, int sampling) {
            this.blur = blur;
            this.sampling = sampling;
            return this;
        }

        public Builder roundedCorners(int rounded, RoundedCornersTransformation.CornerType cornerType) {
            this.rounded = rounded;
            this.cornerType = cornerType;
            return this;
        }

        public Builder borderRound(int borderWidth, int borderColor, int borderRound) {
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            this.borderRound = borderRound;
            return this;
        }

        public Builder centerCrop() {
            this.isCenterCrop = true;
            return this;
        }

        public Builder circleCrop() {
            this.isCircleCrop = true;
            return this;
        }

        public Builder override(int imageWidth, int imageHeight) {
            this.imageHeight = imageHeight;
            this.imageWidth = imageWidth;
            return this;
        }

        public void show(ImageView imageView) {
            ImageConfig.getInstance()
                    .builder(this)
                    .show(imageView);
        }

        public void show(ImageView imageView, ImageCallBack imageCallBack) {
            ImageConfig.getInstance()
                    .builder(this)
                    .imageCallBack(imageView, imageCallBack);
        }

        public void pause() {
            ImageConfig.getInstance().pause();
        }

        public void resume() {
            ImageConfig.getInstance().resume();
        }

        public void clearMemoryCache(View view) {
            ImageConfig.getInstance().clearMemoryCache(view);
        }

        public void clearMemory() {
            ImageConfig.getInstance().clearMemory();
        }
    }

    public static int getPlaceholder() {
        int a = new Random().nextInt(3);
        if (a == 0) {
            return R.drawable.shape_image_default;
        } else if (a == 1) {
            return R.drawable.shape_image_default02;
        } else {
            return R.drawable.shape_image_default03;
        }
    }
}
