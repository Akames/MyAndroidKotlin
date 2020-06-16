package com.akame.imagepicker.wigth

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akame.developkit.util.ScreenUtil

class SpacesItemDecoration : RecyclerView.ItemDecoration() {

    private val offset: Int by lazy {
        ScreenUtil.dip2px(3f)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager as GridLayoutManager
        val count = layoutManager.spanCount

        val speed = offset / 3
        when ((pos + 1) % count) {
            1 -> {
                outRect.right = speed * 2
            }
            2 -> {
                outRect.left = speed
                outRect.right = speed
            }
            0 -> {
                outRect.left = speed * 2
            }
        }

        if (pos >= 3) {
            outRect.top = offset
        }
    }
}