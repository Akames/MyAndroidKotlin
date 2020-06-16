package com.akame.myandroid_kotlin

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.PopupWindow
import com.akame.developkit.util.ScreenUtil

class CommentPop(context: Context) : PopupWindow() {
    private var gestureDetector: GestureDetector
    init {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ScreenUtil.getScreenHeight() / 3 * 2
        contentView = LayoutInflater.from(context).inflate(R.layout.item_layout_comment, null)
        animationStyle = R.style.popwin_anim
        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent?): Boolean {
                    return true
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    Log.e("tag", "---x----- $distanceX----y----  $distanceY")
                    contentView.y -= distanceY
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }
            })

        contentView.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }

    }


}