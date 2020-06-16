package com.akame.developkit.wigth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.akame.developkit.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialog() : BottomSheetDialogFragment() {
    private lateinit var contentView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetStyle)
        contentView = LayoutInflater.from(context).inflate(getLayoutId(), null)
        dialog?.window?.setWindowAnimations(R.style.dialog_anim)
        contentView.layoutParams = ViewGroup.LayoutParams(getWindowWidth(), getWindowHeight())
        createView(contentView)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
    }

    abstract fun getLayoutId(): Int

    abstract fun createView(view: View)

    abstract fun getWindowWidth(): Int

    abstract fun getWindowHeight(): Int


}