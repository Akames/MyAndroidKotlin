package com.akame.developkit.util

import android.os.Environment
import com.akame.developkit.BaseApp
import java.io.File

object FileUtil {
    /**
     * 创建文件
     * @param filePath 文件路径
     */
    fun createFile(filePath: String): File {
        val file = File(filePath)
        //如果存在 删除以前的
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    /**
     * 创建文件夹
     */
    fun createDir(dirPath: String): File {
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * 获取缓存路径
     *
     * @param context
     * @return /storage/emulated/0/Android/data/{$包名}/cache
     */
    fun getCachePath(): String {
        return if (Environment.MEDIA_MOUNTED == Environment
                .getExternalStorageState() || !Environment.isExternalStorageRemovable()
        ) {
            BaseApp.app.externalCacheDir!!.path
        } else {
            BaseApp.app.cacheDir.path
        }
    }

    /**
     * 删除图片
     */
    fun deleteFile(filePath: String?) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 获取文件大小
     */
    fun getFileSize(filePath: String): Long = File(filePath).length()
}