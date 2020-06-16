package com.akame.myandroid_kotlin

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akame.developkit.util.ScreenUtil
import com.akame.developkit.wigth.BaseBottomSheetDialog

class Test : BaseBottomSheetDialog() {
    override fun getLayoutId(): Int = R.layout.item_layout_comment
    override fun createView(view: View) {
        val rvContent = view.findViewById<RecyclerView>(R.id.rv_content)
        rvContent.layoutManager = LinearLayoutManager(context)
        rvContent.adapter = CommentButtonFragment.RvAdapter()
    }

    override fun getWindowWidth(): Int = ViewGroup.LayoutParams.MATCH_PARENT

    override fun getWindowHeight(): Int = ScreenUtil.getScreenHeight() / 3 * 2

}