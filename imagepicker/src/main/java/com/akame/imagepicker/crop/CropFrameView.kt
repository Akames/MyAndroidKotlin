package com.akame.imagepicker.crop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.akame.developkit.util.ScreenUtil


class CropFrameView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val mHorizontalPadding = ScreenUtil.dip2px(16f)
    private val clipWidth = ScreenUtil.getScreenWidth() - mHorizontalPadding * 2f
    private var paint: Paint = Paint()
    private var borderPaint = Paint()
    private val xesModeler: Xfermode by lazy {
        PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }
    private lateinit var rectF: RectF

    init {
        borderPaint.color = Color.WHITE
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = ScreenUtil.dip2px(1f).toFloat()
        borderPaint.isAntiAlias = true
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rectF = RectF(
            mHorizontalPadding.toFloat(), (height - clipWidth) / 2,
            (width - mHorizontalPadding).toFloat(),
            (height + clipWidth) / 2
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 通过XFerMode的DST_OUT来产生中间的透明裁剪区域，一定要另起一个Layer（层）
        val saved = canvas?.saveLayer(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            paint,
            Canvas.ALL_SAVE_FLAG
        )
        // 绘制背景
        canvas?.drawColor(Color.parseColor("#B3000000"))
        paint.xfermode = xesModeler
        //绘制中间白色的矩形蒙层
        canvas?.drawRect(rectF, paint)
        canvas?.restoreToCount(saved!!)
        //绘制白色的矩形边框
        canvas?.drawRect(rectF, borderPaint)
    }

    fun getFrameRectF(): RectF {
        return rectF
    }

}