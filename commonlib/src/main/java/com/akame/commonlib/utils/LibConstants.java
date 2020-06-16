package com.akame.commonlib.utils;

import android.util.LruCache;

import com.akame.commonlib.BuildConfig;
import com.akame.commonlib.CommonLib;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 * @Author: Restring
 * @Date: 2018/4/23
 * @Description 全局公共类
 */
public class LibConstants {
    public static final boolean IS_DEBUG = BuildConfig.DEBUG;
    private static LruCache lruCache;
    private static DiskLruCache diskLruCache;
    public static final String PATH_CACHE = getPathData() + "/NetCache";
    private static final long MAX_CACHE_SIZE = Runtime.getRuntime().maxMemory() / 4;

    public static String getPathData() {
        return CommonLib.getApplication().getCacheDir().getAbsolutePath() + File.separator + "data";
    }

    /**
     * 内存缓存
     *
     * @return LruCache 缓存
     */
    public static LruCache getLruCache() {
        synchronized (LibConstants.class) {
            if (lruCache == null) {
                lruCache = new LruCache((int) MAX_CACHE_SIZE);
            }
            return lruCache;
        }
    }

    /**
     * 磁盘缓存
     *
     * @return DisLruCache 缓存
     */
    public static DiskLruCache getDiskLruCache() {
        synchronized (LibConstants.class) {
            if (diskLruCache == null) {
                try {
                    diskLruCache = DiskLruCache.open(getFileDir("test"),
                            1,
                            1,
                            MAX_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return diskLruCache;
        }
    }


    /**
     * 获取缓存文件地址
     *
     * @param cacheFileName 设置缓存文件名
     * @return 返回缓存地址
     */
    public static File getFileDir(String cacheFileName) {
        File file = new File(CommonLib.getApplication().getCacheDir().getPath(), cacheFileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}
