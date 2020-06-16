package com.akame.myandroid_kotlin

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator

class TestLine(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var lineSpace = 0f
    private var valueSpace = 0f

    init {
        paint.color = Color.RED
        paint.strokeWidth = 6f
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        startX = width / 2f
        startY = height / 2f
        endX = width / 2f
        endY = startY
        lineSpace = startX - 100f
        valueSpace = startX
        startAnimator()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawLine(startX, startY, endX, endY, paint)
    }

    private fun startAnimator() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            startX = valueSpace - value * valueSpace
            endX = valueSpace + value * valueSpace
            paint.alpha = (255 * (1-value)).toInt()
            invalidate()
        }
        animator.duration = 1000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }
}