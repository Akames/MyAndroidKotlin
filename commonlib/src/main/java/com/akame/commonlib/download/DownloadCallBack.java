package com.akame.commonlib.download;

/**
 * @Author: Akame
 * @Date: 2018/12/20
 * @Description:
 */
public interface DownloadCallBack {
    void onStart();

    void onProgress(int currentLength);

    void onFinish(String localPath);

    void onFailure();
}
