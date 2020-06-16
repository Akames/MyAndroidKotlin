package com.akame.commonlib.utils;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

/**
 * @Author: Restring
 * @Date: 2018/5/28
 * @Description:
 */
public class LogUtil {

    public static void d(Object log) {
        if (LibConstants.IS_DEBUG) Logger.d(log);
    }

    public static void e(String log) {
        if (LibConstants.IS_DEBUG) Logger.e(log, log);
    }

    public static void xml(String xml) {
        if (LibConstants.IS_DEBUG) Logger.xml(xml);
    }

    public static void json(String json) {
        if (LibConstants.IS_DEBUG && !TextUtils.isEmpty(json) && GsonUtil.isJson(json)) {
            Logger.json(json);
        }
    }
}
