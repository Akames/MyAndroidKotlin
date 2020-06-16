package com.akame.commonlib.download;

import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.akame.commonlib.utils.FileUtil;
import com.akame.commonlib.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @Author: Akame
 * @Date: 2018/12/20
 * @Description: 下载工具类
 */
public class DownloadUtil {
    private static final String PATH_CHALLENGE_FILE = Environment.getExternalStorageDirectory() + "/DownloadFile";
    //视频下载相关
    protected DownLoaderApi mApi;
    private Call<ResponseBody> mCall;
    private File mFile;
    private String mDownloadPath; //下载到本地的视频路径

    public DownloadUtil() {
        if (mApi == null) {
            //初始化网络请求接口
            mApi = ApiHelper.getInstance()
                    .buildRetrofit("https://sapi.daishumovie.com/")
                    .createService(DownLoaderApi.class);
        }
    }

    public void downloadFile(String url, final DownloadCallBack downloadListener) {
        LogUtil.d("=====" + url);
        String name = url;
        //通过Url得到文件并创建本地文件
        if (FileUtil.createOrExistsDir(PATH_CHALLENGE_FILE)) {
            int i = name.lastIndexOf('&');//一定是找最后一个'/'出现的位置 这里&是项目特殊情况 一般是/
            if (i != -1) {
                name = name.substring(i);
                mDownloadPath = PATH_CHALLENGE_FILE + name;
            }
        }
        if (TextUtils.isEmpty(mDownloadPath)) {
            LogUtil.e("downloadFile: 存储路径为空了");
            return;
        }
        //建立一个文件
        mFile = new File(mDownloadPath);
        if (mFile.exists()) {
            mFile.delete();
        }
//        if (!FileUtil.isFileExists(mFile) && FileUtil.createOrExistsFile(mFile)) {
        if (mApi == null) {
            LogUtil.e("downloadFile: 下载接口为空了");
            return;
        }
        mCall = mApi.downloadFile(url);
        mCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                //下载文件放在子线程
                   /* mThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //保存到本地
                            writeFile2Disk(response, mFile, downloadListener);
                        }
                    };*/
                Observable.create((ObservableOnSubscribe<Integer>) e -> {
                    downloadListener.onStart();
                    long currentLength = 0;
                    InputStream is = response.body().byteStream(); //获取下载输入流
                    long totalLength = response.body().contentLength(); //获取下载总长度

                    OutputStream os = new FileOutputStream(mFile); //输出流
                    int len;
                    byte[] buff = new byte[1024];
                    while ((len = is.read(buff)) != -1) {
                        os.write(buff, 0, len);
                        currentLength += len;
                        //LogUtil.d(  "当前进度: " + currentLength);
                        //计算当前下载百分比，并经由回调传出
                            /*downloadListener.onProgress((int) (100 * currentLength / totalLength));
                            //当百分比为100时下载结束，调用结束回调，并传出下载后的本地路径
                            if ((int) (100 * currentLength / totalLength) == 100) {
                                downloadListener.onFinish(mDownloadPath); //下载完成
                            }*/
                        int l = (int) (100 * currentLength / totalLength);
                        e.onNext(l);
                        if (l == 100) {
                            e.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Integer integer) {
                                downloadListener.onProgress(integer);
                            }

                            @Override
                            public void onError(Throwable e) {
                                downloadListener.onFailure();
                            }

                            @Override
                            public void onComplete() {
                                downloadListener.onFinish(mDownloadPath);
                            }
                        });
//                mThread.start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                downloadListener.onFailure(); //下载失败
            }
        });
        /*} else {
            downloadListener.onFinish(mDownloadPath); //下载完成
        }*/
    }

    //将下载的文件写入本地存储
    private void writeFile2Disk(Response<ResponseBody> response, File file, DownloadCallBack downloadListener) {
        downloadListener.onStart();
        long currentLength = 0;
        OutputStream os = null;

        InputStream is = response.body().byteStream(); //获取下载输入流
        long totalLength = response.body().contentLength();

        try {
            os = new FileOutputStream(file); //输出流
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
//                LogUtil.d(  "当前进度: " + currentLength);
                //计算当前下载百分比，并经由回调传出
                downloadListener.onProgress((int) (100 * currentLength / totalLength));
                //当百分比为100时下载结束，调用结束回调，并传出下载后的本地路径
                if ((int) (100 * currentLength / totalLength) == 100) {
                    downloadListener.onFinish(mDownloadPath); //下载完成
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close(); //关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close(); //关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
