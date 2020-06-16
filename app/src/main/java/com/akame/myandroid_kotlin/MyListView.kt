package com.akame.myandroid_kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ListView

class MyListView(context: Context, attributeSet: AttributeSet) : ListView(context, attributeSet) {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_MOVE) {

                parent.requestDisallowInterceptTouchEvent(false)
        } else if (ev?.action == MotionEvent.ACTION_UP) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isTop(): Boolean = firstVisiblePosition == 0 && getChildAt(0).top == 0

    private fun isButton(): Boolean =
        lastVisiblePosition == count - 1 && height >= getChildAt(lastVisiblePosition - firstVisiblePosition).bottom

}