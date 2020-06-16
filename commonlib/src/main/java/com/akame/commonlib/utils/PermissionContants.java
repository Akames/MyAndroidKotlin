package com.akame.commonlib.utils;

import android.Manifest;

/**
 * @Author: Akame
 * @Date: 2019/4/28
 * @Description: 权限控制列表
 */
public class PermissionContants {
    //读写权限
    public static final String[] READ_WRITE_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    //拍照权限
    public static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};

    //读写和拍照 用于选取图片和拍照
    public static final String[] READ_WRITE_CAMERA_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA};

    public static String getPermissionName(String... permissions) {
        if (permissions == READ_WRITE_PERMISSION) {
            return "读写";
        } else if (permissions == CAMERA_PERMISSION) {
            return "相机/拍照";
        } else if (permissions == READ_WRITE_CAMERA_PERMISSION) {
            return "读写和相机";
        } else {
            return "";
        }
    }
}
