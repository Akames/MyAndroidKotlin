package com.akame.developkit.util

import android.app.Activity
import android.view.WindowManager

class CommentUtil {

    /**
     * activity 设置背景透明度
     */
    private fun setBackgroundAlpha(activity: Activity, alpha: Float) {
        val attr = activity.window.attributes
        attr.alpha = alpha
        if (alpha == 1f) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        activity.window.attributes = attr
    }
}