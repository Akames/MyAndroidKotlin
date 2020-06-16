package com.akame.commonlib.image.loader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.akame.commonlib.CommonLib;
import com.akame.commonlib.utils.ScreenUtil;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * @Author: Restring
 * @Date: 2018/8/13
 * @Description: 圆角边框实现
 */
public class GlideCircleTransform extends BitmapTransformation {
    private Paint mBorderPaint;
    private float mBorderWidth;
    private int round;

    public GlideCircleTransform(Context context) {
        this(context, 4, Color.BLACK, 20);
    }

    public GlideCircleTransform(Context context, int borderWidth, int borderColor, int round) {
        mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorderPaint = new Paint();
        mBorderPaint.setDither(true);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        this.round = round;
    }

    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if (round != 0)
            return filletCrop(pool, toTransform);
        else
            return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        if (mBorderPaint != null) {
            float borderRadius = r - mBorderWidth / 2;
            canvas.drawCircle(r, r, borderRadius, mBorderPaint);
        }
        return result;
    }

    private Bitmap filletCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int w = Math.min(source.getWidth(), ScreenUtil.getScreenWidth(CommonLib.getApplication()));
        int h = Math.min(source.getHeight(), ScreenUtil.getScreenHeight(CommonLib.getApplication()));
        int width = (int) (w - mBorderWidth);
        int height = (int) (h - mBorderWidth);
        Bitmap squared = Bitmap.createBitmap(source, 0, 0, width, height);
        Bitmap result = pool.get(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, width, height), round, round, paint);
        if (mBorderPaint != null) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, width, height), round, round, mBorderPaint);
        }
        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
