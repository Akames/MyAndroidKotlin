package com.akame.myandroid_kotlin

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akame.developkit.util.ScreenUtil
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyBottomSheetDialog(context: Context) : BottomSheetDialog(context,R.style.TransparentBottomSheetStyle) {
    private var view: View = layoutInflater.inflate(R.layout.item_layout_comment, null).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ScreenUtil.getScreenHeight() / 3 * 2
        )
    }

    init {
        val rvContent = view.findViewById<RecyclerView>(R.id.rv_content)
        rvContent.layoutManager = LinearLayoutManager(context)
        rvContent.adapter = CommentButtonFragment.RvAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view)
        (view.parent as View).setBackgroundColor(context.resources.getColor(android.R.color.transparent))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(false)
    }

    override fun show() {
        super.show()
        window.setWindowAnimations(R.style.popwin_anim)
    }

}