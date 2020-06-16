package com.akame.videoplay.unit

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener

class MyProgressView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRect = RectF()
    private val paddingSpace = 20
    private var degrees = 0f
    private var angle = 0f
    private val animatorSet = AnimatorSet()
    private var startAngle = -90f
    private var isRepeat = false
    private var isShow = false // 是否显示  只有显示的时候才执行动画刷新界面

    init {
        mPaint.color = Color.parseColor("#a8edea")
        mPaint.strokeWidth = 8f
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {
            degrees = it.animatedValue as Float * 360
//            invalidate()
        }
        animator.interpolator = LinearInterpolator()
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.startDelay = 500

        val animatorAngle = ValueAnimator.ofFloat(0f, 1f)
        animatorAngle.addUpdateListener {
            val value = it.animatedValue as Float
            if (value != 1f && isShow) {
                angle = if (isRepeat) {
                    -(1 - value) * 330
                } else {
                    value * 330
                }
                invalidate()
            }
        }
        animatorAngle.addListener(onRepeat = {
            isRepeat = !isRepeat
            startAngle += angle
        })
        animatorAngle.interpolator = LinearInterpolator()
        animatorAngle.duration = 800
        animatorAngle.repeatCount = ValueAnimator.INFINITE
        animatorAngle.repeatMode = ValueAnimator.RESTART
        animatorSet.playTogether(animator,animatorAngle)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mRect.set(0f + paddingSpace, 0f + paddingSpace, width.toFloat() - paddingSpace, height.toFloat() - paddingSpace)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.rotate(degrees, width / 2f, height / 2f)
        canvas?.drawArc(mRect, startAngle, angle, false, mPaint)
    }

    fun isShow(isShow: Boolean) {
        this.isShow = isShow
        if (isShow) {
            if (!animatorSet.isStarted) {
                animatorSet.start()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && animatorSet.isPaused) {
                animatorSet.resume()
            }
        } else {
            if (animatorSet.isRunning) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    animatorSet.pause()
                }
            }
        }
    }

    fun onCancel() {
        animatorSet.cancel()
    }

}