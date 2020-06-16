package com.akame.myandroid_kotlin

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.min

class MyView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private var paint: Paint
    private var path: Path
    private lateinit var rectF: RectF
    private val bitmap: Bitmap
    private val matrixs = Matrix()
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val anmin: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 360f).apply {
            addUpdateListener {
                matrixs.postRotate(1f, cententX, cententY)
                invalidate()
            }
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = 60 * 1000
        }
    }

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        path = Path()
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_bitmap)

        post {
            anmin.start()
            invalidate()
        }
    }

    var cententX = 0f
    var cententY = 0f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val sWidth = width / 2f
        val data = (width - sWidth) / 2
        rectF = RectF(data, data, sWidth + data, sWidth + data)

        val s = sWidth * 1f / min(bitmap.width, bitmap.height)


        val xx = (width * 1f - bitmap.width * s) / 2
        val yy = data

        cententX = width / 2f
        cententY = data + bitmap.height * s / 2

        matrixs.setTranslate(xx, yy)
        matrixs.preScale(s, s)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val sc = canvas?.saveLayer(0f, 0f, width * 1f, height * 1f, paint, Canvas.ALL_SAVE_FLAG)
        canvas?.drawOval(rectF, paint) //绘制模版 比如圆或者其他图像
        paint.xfermode = xfermode //设置切割模式
        canvas?.drawBitmap(bitmap, matrixs, paint) //设置目标值
        paint.xfermode = null
        canvas?.restoreToCount(sc!!)
    }


}