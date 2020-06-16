package com.akame.developkit.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.akame.developkit.BaseApp
import java.io.File

object SystemUtil {

    /**
     * 获取版本号
     */
    fun getVersionCode(): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BaseApp.app.packageManager.getPackageInfo(BaseApp.app.packageName, 0).longVersionCode
        } else {
            BaseApp.app.packageManager.getPackageInfo(BaseApp.app.packageName, 0).versionCode.toLong()
        }

    /**
     * 获取版本名称
     */
    fun getVersionName() = BaseApp.app.packageManager.getPackageInfo(BaseApp.app.packageName, 0).versionName

    /**
     * 获取包名
     */
    fun getPackName() = BaseApp.app.packageName

    /**
     * 是否有SD卡
     */
    fun hasSDCard() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED


    /**
     *  获取WIFI-mac地址
     */
    @SuppressLint("HardwareIds", "WifiManagerPotentialLeak")
    fun getMacAddress(): String {
        val wifiManager = BaseApp.app.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        if (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
            wifiManager.isWifiEnabled = false
        }
        return wifiInfo.macAddress
    }

    /**
     * 判断是否程序已安装
     */
    fun isInstallApk(packName: String): Boolean {
        val packManger = BaseApp.app.packageManager
        val packList = packManger.getInstalledPackages(0) // 获取系统已经安装程序列表
        packList.forEach {
            if (it.packageName == packName) {
                return true
            }
        }
        return false
    }

    /**
     * 安装apk
     */
    fun installApk(apkPath: String, authority: String) {
        val apk = File(apkPath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(BaseApp.app, authority, apk)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive")
        }
        BaseApp.app.startActivity(intent)
    }

    /**
     * 跳转外部应用安装权限
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun intentInstallApkPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        BaseApp.app.startActivity(intent)
    }

}