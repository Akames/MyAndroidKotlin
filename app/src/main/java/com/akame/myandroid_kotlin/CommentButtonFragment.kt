package com.akame.myandroid_kotlin

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akame.developkit.util.ScreenUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CommentButtonFragment : BottomSheetDialogFragment() {

    private lateinit var contentView :View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = LayoutInflater.from(context).inflate(R.layout.item_layout_comment, null)
        contentView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
        ScreenUtil.getScreenHeight() / 3 * 2
        )
        val rvContent = contentView.findViewById<RecyclerView>(R.id.rv_content)
        rvContent.layoutManager = LinearLayoutManager(context)
        rvContent.adapter = RvAdapter()
        return contentView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NO_INPUT, R.style.TransparentBottomSheetStyle)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TransparentBottomSheetStyle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        //设置点击外部可消失
        dialog.setCanceledOnTouchOutside(false)
        //设置使软键盘弹出的时候dialog不会被顶起
        val win = dialog.window
//        val params = win.attributes
//        win.setSoftInputMode(params.SOFT_INPUT_ADJUST_NOTHING)
        //这里设置dialog的进出动画
        win.setWindowAnimations(R.style.popwin_anim)
        return dialog
    }


    class RvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_rv_content, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = 20

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }

    }

}