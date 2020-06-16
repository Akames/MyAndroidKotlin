package com.akame.myandroid_kotlin

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

class MyLinearLayout(context: Context,attributeSet: AttributeSet) : LinearLayout(context,attributeSet){
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.e("tag","viewGroup--dispatchTouchEvent----")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.e("tag","viewGroup--onInterceptTouchEvent----")
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e("tag","viewGroup--onTouchEvent----")
        return super.onTouchEvent(event)
    }

}