package com.akame.developkit.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.akame.developkit.BaseApp

/**
 * 系统高度测量
 */
object ScreenUtil {
    private var screenWidth = 0 //屏幕宽度
    private var screenHeight = 0 //屏幕高度
    private var statusBarHeight = 0 //状态栏高度

    /**
     * dp转化px
     */
    fun dip2px(dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    /**
     * px转话dp
     */
    fun px2dip(pxValue: Float): Int {
        val scale = BaseApp.app.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(): Int {
        val dm = DisplayMetrics()
        val wm = BaseApp.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        screenWidth = dm.widthPixels
        return screenWidth
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(): Int {
        val dm = DisplayMetrics()
        val wm = BaseApp.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        screenHeight = dm.heightPixels
        return screenHeight
    }


    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(): Int {
        if (statusBarHeight != 0) {
            return statusBarHeight
        }
        //获取状态栏高度的资源id
        val resourceId =
            BaseApp.app.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = BaseApp.app.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

}