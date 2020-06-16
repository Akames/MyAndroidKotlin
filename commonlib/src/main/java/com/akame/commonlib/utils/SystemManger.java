package com.akame.commonlib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.akame.commonlib.CommonLib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: Akame
 * @Date: 2019/2/15
 * @Description: 系统管理类
 */
public class SystemManger {
    //提示用户开放安装权限返回码
    public static final int REQUEST_INSTALL_PERMISSIONS_CODE = 0X145;
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode() {
        try {
            PackageManager manager = CommonLib.getApplication().getPackageManager();
            PackageInfo info = manager.getPackageInfo(CommonLib.getApplication().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取版本名字
     *
     * @return 版本名
     */
    public static String getVersionName() {
        try {
            PackageManager manager = CommonLib.getApplication().getPackageManager();
            PackageInfo info = manager.getPackageInfo(CommonLib.getApplication().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当前应用程序的包名
     *
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppPackName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 是否有内存卡
     *
     * @return 有或者没有内存卡
     */
    public static boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取uuid
     */
    @SuppressLint("MissingPermission")
    public static String getSystemUUID() {
        final TelephonyManager tm = (TelephonyManager) CommonLib.getApplication().getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();//设备唯一号码
        tmSerial = "" + tm.getSimSerialNumber();//sim 卡标识
        androidId = ""
                + android.provider.Settings.Secure.getString(
                CommonLib.getApplication().getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);//在设备首次启动时，系统会随机生成一个64位的数字
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    /**
     * 获取MAC地址
     */
    public static String getMacAddress() {
        String macAddress = null;
        WifiManager wifiManager =
                (WifiManager) CommonLib.getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
        if (!wifiManager.isWifiEnabled()) {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        return macAddress;
    }

    /**
     * 检查该程序是否安装
     */
    public static boolean isInstallApk(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfo != null) {
            for (int i = 0; i < packageInfo.size(); i++) {
                String pn = packageInfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

    /**
     * 安装apk
     */
    public static void installApk(String filePath, Activity activity) {
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    activity
                    , "com.roong.metlhome.fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        activity.startActivity(intent);
    }

    /**
     * 提示用户前往开启安装apk权限
     * @param activity
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void intentInstallApkPermissions(Activity activity) {
        ToastUtil.showToast("安装应用需要打开未知来源权限，请去设置中开启权限");
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        activity.startActivityForResult(intent,REQUEST_INSTALL_PERMISSIONS_CODE);
    }
}
