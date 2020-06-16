package com.akame.commonlib.utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @Author: Administrator
 * @Date: 2018/11/5
 * @Description: 软键盘管理类
 */
public class InputMethodUtil {

    /**
     * 显示软键盘
     *
     * @param view 软键盘接受数据View 最好是editText
     */
    public static void showKeyBoard(View view) {
        new Handler().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                view.requestFocus();
                imm.showSoftInput(view, 0);
            }
        }, 0);
    }

    /**
     * 隐藏软键盘
     *
     * @param view 跟上面一样
     */
    public static void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 切换软键盘状态 如果是弹出状态就隐藏，反之亦然
     *
     * @param view 跟上面一样
     */
    public static void toggleSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, 0);
        }
    }
}
