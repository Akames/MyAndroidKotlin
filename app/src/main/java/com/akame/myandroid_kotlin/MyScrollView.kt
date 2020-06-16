package com.akame.myandroid_kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class MyScrollView(context: Context, attributeSet: AttributeSet) :
    ScrollView(context, attributeSet) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            return false
        }
        return super.onInterceptTouchEvent(ev)
    }
}