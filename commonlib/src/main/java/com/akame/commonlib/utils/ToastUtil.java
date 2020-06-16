package com.akame.commonlib.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akame.commonlib.CommonLib;
import com.akame.commonlib.R;


/**
 * @Author: Restring
 * @Date: 2018/5/28
 * @Description:
 */
public class ToastUtil {
    private static Toast toast;
    private static View backgroundView = LayoutInflater.from(CommonLib.getApplication())
            .inflate(R.layout.layout_toast_bg, null);

    private static TextView tvData = backgroundView.findViewById(R.id.tv_date);

    public static void showToast(String msg) {
        if (toast == null) {
            toast = new Toast(CommonLib.getApplication());
            toast.setView(backgroundView);
            toast.setGravity(Gravity.CENTER, 0, -500);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        tvData.setText(msg);
        toast.show();
    }


}
