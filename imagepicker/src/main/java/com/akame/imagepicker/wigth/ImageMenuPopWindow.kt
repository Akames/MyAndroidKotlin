package com.akame.imagepicker.wigth

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akame.imagepicker.R
import com.akame.imagepicker.adapter.SelectPopWindowAdapter
import com.akame.imagepicker.been.MenuBeen

class ImageMenuPopWindow(val context: Context, selectList: ArrayList<MenuBeen>) : PopupWindow() {
    private fun getDialogLayoutRes(): Int = R.layout.dialog_select_image

    init {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        val view = LayoutInflater.from(context).inflate(getDialogLayoutRes(), null)
        contentView = view
        val rvContent = view.findViewById<RecyclerView>(R.id.rv_content)
        rvContent.layoutManager = LinearLayoutManager(context)
        val adapter = SelectPopWindowAdapter(selectList)
        rvContent.adapter = adapter

        setBackgroundDrawable(ColorDrawable(0))
        isOutsideTouchable =
            true //巨坑 有时间可以继续研究，点击按钮时候 手指还在down状态就会触发dismiss 并且对控件进行onTouchEvent拦截都要快
        adapter.itemClickListener = {
            itemClickListener(it)
        }
        //解决pop设置了button点击弹出消失 又设置了点击外部消失 冲突的问题，如果不加后面代码 会出现 当pop 为show的时候 再次点击 在手指还在屏幕的时候就会触发消失，手指抬起就触发显示
        isFocusable = true
        setTouchInterceptor(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_OUTSIDE) {
                    dismiss()
                    return true
                }
                return false
            }
        })
    }

    var itemClickListener: (Int) -> Unit = {}
}