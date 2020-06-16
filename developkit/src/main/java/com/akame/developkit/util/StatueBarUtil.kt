package com.akame.developkit.util

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager


object StatueBarUtil {

    /**
     * 状态栏透明 5.0以下的不做处理
     */
    fun transparentStatusBar(activity: Activity, toolBar: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //需要设置这个flag contentView才能延伸到状态栏
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                //状态栏覆盖在contentView上面，设置透明使contentView的背景透出来
                window.statusBarColor = Color.TRANSPARENT;
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }

            toolBar?.setPadding(
                toolBar.paddingLeft,
                toolBar.paddingTop + ScreenUtil.getStatusBarHeight(),
                toolBar.paddingRight,
                toolBar.paddingBottom
            )
        }
    }

    /**
     * 设置状态栏颜色是否为白色
     */
    fun setLightMode(activity: Activity, isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLight) {
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        } else {
            if (RomUtil.isMiui) {
                setMIUIStatusBarLightIcon(activity, isLight)
            } else if (RomUtil.isFlyme) {
                setMeiZuStatusBarLightIcon(activity, isLight)
            }
        }
    }

    /**
     * 修改 MIUI V6  以上状态栏颜色
     */
    private fun setMIUIStatusBarLightIcon(activity: Activity, isLight: Boolean) {
        val window = activity.window;
        if (window != null) {
            val clazz = window.javaClass
            try {
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams");
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                val darkModeFlag = field.getInt(layoutParams);
                val extraFlagField = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                extraFlagField.invoke(
                    window,
                    if (isLight) 0 else darkModeFlag,
                    darkModeFlag
                )
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 修改魅族状态栏字体颜色 Flyme 4.0
     */
    private fun setMeiZuStatusBarLightIcon(activity: Activity, isLight: Boolean) {
        try {
            val lp = activity.window.attributes
            val lightFlag =
                WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meiZuFlags =
                WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
            lightFlag.isAccessible = true
            meiZuFlags.isAccessible = true
            val bit = lightFlag.getInt(null)
            var value = meiZuFlags.getInt(lp)
            value = if (isLight) {
                value and bit.inv()
            } else {
                value or bit
            }
            meiZuFlags.setInt(lp, value)
            activity.window.attributes = lp
        } catch (e: Exception) {
            //e.printStackTrace();
        }
    }
}