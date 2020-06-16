package com.akame.commonlib.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.akame.commonlib.R;

import java.lang.reflect.Method;

/**
 * @Author: Akame
 * @Date: 2019/4/9
 * @Description: 刘海屏适配方案
 */
public class DisplayCutoutUtil {

    /**
     * adapt fullScreen mode
     *
     * @param mActivity a
     */
    public static boolean openFullScreenModel(Activity mActivity) {
        boolean isPaddingTop = false;
        try {
            if (needAdaptNotch(mActivity)) {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                //针对9.0适配
                // oppo 中设置对于下面代码无效 只能走第二条路线
                if (Build.VERSION.SDK_INT >= 28 && !RomUtil.isOppo()) {
                    lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                } else {
                    //第二种适配方案理论上适配所有，将背景设置为黑色，将布局向下摞动状态栏动高度，一般刘海的高度约等于状态栏的高度
                    View decorView = mActivity.getWindow().getDecorView();
                    decorView.setBackgroundColor(mActivity.getResources().getColor(R.color.black));
                    decorView.setPadding(decorView.getLeft(), ScreenUtil.getStatusBarHeight(mActivity), decorView.getPaddingRight(), decorView.getPaddingBottom());
                    isPaddingTop = true;
                }
                mActivity.getWindow().setAttributes(lp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isPaddingTop;
    }

    /**
     * need to adapt Notch screen
     *
     * @return true otherwise false
     */
    public static boolean needAdaptNotch(Context context) {
        if (RomUtil.isEmui()) {
            return isHuaweiNotch((context));
        } else if (RomUtil.isOppo()) {
            return isOppoNotch(context);
        } else if (RomUtil.isVivo()) {
            return isVivoNotch(context);
        } else if (RomUtil.isMiui()) {
            return isMiNotch(context);
        } else {
            return false;
        }
    }

    /**
     * xiaoMI
     *
     * @param c
     * @return
     */
    private static boolean isMiNotch(Context c) {
        boolean ret = false;
        try {
            ClassLoader cl = c.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            Method get = SystemProperties.getMethod("getInt", String.class, int.class);
            ret = (Integer) get.invoke(SystemProperties, "ro.miui.notch", 0) == 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    /**
     * huawei
     *
     * @param context c
     * @return hasNotch
     */
    private static boolean isHuaweiNotch(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            LogUtil.e("hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtil.e("hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            LogUtil.e("hasNotchInScreen Exception");
        }
        return ret;
    }

    /**
     * OPPO
     *
     * @param context Context
     * @return hasNotch
     */
    private static boolean isOppoNotch(Context context) {
        try {
            return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * VIVO
     * param:
     * 0x00000020表示是否有凹槽;
     * 0x00000008表示是否有圆角。
     *
     * @param context Context
     * @return hasNotch
     */
    private static boolean isVivoNotch(Context context) {
        boolean hasNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("android.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport");
            hasNotch = (boolean) get.invoke(FtFeature, new Object[]{0x00000020});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNotch;
    }
}
