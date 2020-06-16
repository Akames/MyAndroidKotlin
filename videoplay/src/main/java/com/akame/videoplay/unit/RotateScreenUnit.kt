package com.akame.videoplay.unit

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.akame.developkit.util.ScreenUtil
import com.akame.videoplay.VideoControlView
import com.akame.videoplay.VideoPlayView

class RotateScreenUnit {
    private var viewHeight = 0
    /**
     * 旋转屏幕
     */
    fun updatePlayerViewMode(activity: Activity, view: VideoControlView) {
        val orientation = activity.resources.configuration.orientation
        val layoutParams = view.layoutParams
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            //设置view的布局，宽高之类
            layoutParams.width = ScreenUtil.getScreenWidth()
            layoutParams.height = viewHeight
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //转到横屏了。
            //隐藏状态栏
            if (!isStrangePhone()) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
            //设置view的布局，宽高
            if (viewHeight == 0) {
                viewHeight = layoutParams.height
            }
            layoutParams.width = ScreenUtil.getScreenWidth()
            layoutParams.height = ScreenUtil.getScreenHeight()
        }
        view.layoutParams = layoutParams
    }

    private fun isStrangePhone(): Boolean {
        return ("mx5".equals(Build.DEVICE, ignoreCase = true)
                || "Redmi Note2".equals(Build.DEVICE, ignoreCase = true)
                || "Z00A_1".equals(Build.DEVICE, ignoreCase = true)
                || "hwH60-L02".equals(Build.DEVICE, ignoreCase = true)
                || "hermes".equals(Build.DEVICE, ignoreCase = true)
                || "V4".equals(
            Build.DEVICE,
            ignoreCase = true
        ) && "Meitu".equals(Build.MANUFACTURER, ignoreCase = true)
                || "m1metal".equals(Build.DEVICE, ignoreCase = true) && "Meizu".equals(
            Build.MANUFACTURER,
            ignoreCase = true
        ))
    }


    fun changeScreenWindow(
        activity: Activity,
        playView: VideoPlayView,
        container: FrameLayout,
        isSmall: Boolean
    ) {
        if (isSmall) {
            //竖屏模式
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            playView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            removeView(container)
            playView.addView(
                container,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        } else {
            //全屏模式
            if (!isStrangePhone()) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                playView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
            val viewGroup = activity.findViewById<ViewGroup>(android.R.id.content)
            removeView(container)
            viewGroup.addView(
                container,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    private fun removeView(view: View) {
        val parent = view.parent
        if (parent is ViewGroup) {
            parent.removeView(view)
        }
    }

}