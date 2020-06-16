package com.akame.videoplay

import android.content.Context
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.akame.developkit.util.ScreenUtil

class MySeekBar(context: Context) :SeekBar(context){
    init {
        setPadding(0,0,0,0)
        progressDrawable = ContextCompat.getDrawable(context,R.drawable.seek_bar_bottom_bg)
        thumb = null

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),ScreenUtil.dip2px(2f))
    }

    /*override fun onTouchEvent(event: MotionEvent?): Boolean {
        thumb = ContextCompat.getDrawable(context,R.mipmap.ic_seek)
        return super.onTouchEvent(event)
    }*/
}