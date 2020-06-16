package com.akame.commonlib.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: Restring
 * @Date: 2018/4/23
 * @Description:
 */
public class CommonUtils {


    /**
     * 判断2个对象是否相等
     *
     * @param a Object a
     * @param b Object b
     * @return isEqual
     */
    public static boolean isEquals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }


    public static boolean isStringEmpty(String data) {
        if (TextUtils.isEmpty(data) || TextUtils.equals("null", data) || TextUtils.equals("", data.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建极光别名
     *
     * @return 别名
     */
    public static String createJPushAlias() {
//        String alias = String.valueOf(System.currentTimeMillis() * (int) (Math.random() * 100 + 1));
        String randomUUID = UUID.randomUUID().toString();
        String data = randomUUID.split("-")[0] + (int) (Math.random() * 100 + 1);
//        try {
//            return Base64.encodeToString(data.getBytes("utf-8"), Base64.DEFAULT);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        return randomUUID.split("-")[0];
//        }
    }


    public static List<Uri> convert(List<String> data) {
        List<Uri> list = new ArrayList<>();
        for (String d : data) list.add(Uri.parse(d));
        return list;
    }
}
