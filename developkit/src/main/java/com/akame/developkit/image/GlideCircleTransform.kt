package com.akame.developkit.image

import android.content.res.Resources
import android.graphics.*
import com.akame.developkit.util.ScreenUtil
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest
import kotlin.math.min

/**
 * @Author: Restring
 * @Date: 2018/8/13
 * @Description: 圆角边框实现
 */
class GlideCircleTransform(
    borderWidth: Int = 4,
    borderColor: Int = Color.BLACK,
    private val round: Int = 20
) : BitmapTransformation() {
    private val mBorderPaint: Paint?
    private val mBorderWidth: Float = Resources.getSystem().displayMetrics.density * borderWidth

    init {
        mBorderPaint = Paint()
        mBorderPaint.isDither = true
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = borderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = mBorderWidth
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        return if (round != 0)
            filletCrop(pool, toTransform)
        else
            circleCrop(pool, toTransform)
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        val size = (min(source.width, source.height) - mBorderWidth / 2).toInt()
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squared = Bitmap.createBitmap(source, x, y, size, size)
        val result = pool.get(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader =
            BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        if (mBorderPaint != null) {
            val borderRadius = r - mBorderWidth / 2
            canvas.drawCircle(r, r, borderRadius, mBorderPaint)
        }
        return result
    }

    private fun filletCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        val w = min(source.width, ScreenUtil.getScreenWidth())
        val h = min(source.height, ScreenUtil.getScreenHeight())
        val width = (w - mBorderWidth).toInt()
        val height = (h - mBorderWidth).toInt()
        val squared = Bitmap.createBitmap(source, 0, 0, width, height)
        val result = pool.get(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader =
            BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        canvas.drawRoundRect(
            RectF(mBorderWidth, mBorderWidth, width.toFloat(), height.toFloat()),
            round.toFloat(),
            round.toFloat(),
            paint
        )
        if (mBorderPaint != null) {
            canvas.drawRoundRect(
                RectF(
                    mBorderWidth,
                    mBorderWidth,
                    width.toFloat(),
                    height.toFloat()
                ), round.toFloat(), round.toFloat(), mBorderPaint
            )
        }
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }
}
