package com.akame.commonlib.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;

import com.akame.commonlib.CommonLib;
import com.akame.commonlib.abs.ICompressCallBack;
import com.akame.commonlib.utils.FileUtil;
import com.akame.commonlib.utils.LogUtil;
import com.akame.commonlib.utils.ScreenUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: Akame
 * @Date: 2019/2/23
 * @Description: 图片压缩工具类
 */
public class ImageCompressManager {

    private static final int COMPRESS_MAX_SIZE = 300;
    private static final long COMPRESS_SIZE = 1 * 1024 * 1024;

    /**
     * 获取图片缓存地址
     */
    private static String getCacheImagePath() {
        return FileUtil.getCachePath(CommonLib.getApplication()) + "/" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * 质量压缩
     * 不修改图片大小 只修改图片质量 适合高清图片压缩
     *
     * @param path    源文件路径
     * @param quality 压缩比例 或者说是清晰度 100 表示不压缩
     * @return 压缩后的地址 压缩失败的话返回源文件
     */
    public static String qualityCompress(String path, int quality) {
        String outFilePath = getCacheImagePath();
        FileOutputStream fos = null;
        Bitmap bitmap = null;
        try {
            //设置输出图片输出流
            fos = new FileOutputStream(new File(outFilePath));
            //通过路径获取需要压缩图片的bitmap对象
            bitmap = BitmapFactory.decodeFile(path);
            //进行质量压缩 quality = 100 为不压缩
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            return outFilePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return path;
        } finally {
            //释放流
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //释放bitmap
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

    }

    /**
     * 采样率压缩
     * 通过需求的长宽来确来等比例压缩长宽，注意输出的不一定是设置的长宽 适合缩略图压缩
     *
     * @param filePath 源文件路径
     * @return 压缩后的地址 压缩失败的话返回源文件
     */
    public static String sampleCompress(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //设置可以读取文件的宽高
        BitmapFactory.decodeFile(filePath, options); //加载图片
        int sampleSize = getSampleSize(options, reqWidth, reqHeight); // 根据原图的宽高计算出采样率比例
        options.inJustDecodeBounds = false; //关闭读取
        //设置采样率
        options.inSampleSize = sampleSize; //等于2的意思 等比例缩放源图长宽缩短一半
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        //设置输出图片输出流
        String outFilePath = getCacheImagePath();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(outFilePath));
            //将压缩的图片写入到fos里面去
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return outFilePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return filePath;
        } finally {
            //释放流
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //释放bitMap
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }


    /**
     * 获取采样比列
     */
    private static int getSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //源图片的高度和宽度
        final float height = options.outHeight;
        final float width = options.outWidth;
        int inSampleSize = 1;
        //判断需求的宽高是否大于源图宽高 如果小于不进行压缩
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            if (reqWidth >= reqHeight) {
                int m = (int) Math.ceil((width / reqWidth)); //向上取整
                inSampleSize = (m == 1) ? 2 : m; //如果刚好等一的话默认缩小一倍
            } else {
                int m = (int) Math.ceil((height / reqHeight));
                inSampleSize = (m == 1) ? 2 : m;
            }
        }
        return inSampleSize;
    }

    /**
     * 缩放压缩 等比例改变图片的宽高 进而改变图片的内存大小
     *
     * @param filePath 源文件路径
     * @return 压缩路径
     */
    public static String sizeCompress(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap b = BitmapFactory.decodeFile(filePath, options);
        int ratio = getRatioSize(b.getWidth(), b.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(b.getWidth() / ratio, b.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, b.getWidth() / ratio, b.getHeight() / ratio);
        canvas.drawBitmap(b, null, rect, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String outFilePath = getCacheImagePath();
        try {
            FileOutputStream fos = new FileOutputStream(new File(outFilePath));
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return outFilePath;
        } catch (Exception e) {
            e.printStackTrace();
            return filePath;
        } finally {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 计算缩放比
     *
     * @param bitWidth  原图的宽
     * @param bitHeight 原图的高
     * @return 缩放比
     */
    private static int getRatioSize(int bitWidth, int bitHeight) {
        // 图片最大分辨率
        int imageHeight = ScreenUtil.getScreenHeight(CommonLib.getApplication());
//        int imageWidth = ScreenUtil.getScreenWidth(CommonLib.getApplication());a
        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageHeight) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageHeight;
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1;
        return ratio;
    }


    //======================================================================

    /**
     * 压缩到指定大小
     *
     * @param filePath 源文件文件路径
     * @param maxSize  指定大小 单位KB
     * @param isRetain 是否保留原图
     */
    public static String qualityToSize(String filePath, int maxSize, boolean isRetain) {
        LogUtil.d("======压缩的文件路径=====" + filePath);
        int quality = 100;
        maxSize *= 1024;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxSize) {
            baos.reset(); //每次都置空baos
            quality -= 10; //每次都去减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos); //进行压缩
        }
        String outFilePath = getCacheImagePath();
        LogUtil.d("=====输出的文件路径====" + outFilePath);
        try {
            FileOutputStream fos = new FileOutputStream(new File(outFilePath)); //将压缩的文件写进设置好目录文件中
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            if (!isRetain) {
                FileUtil.deleteFile(new File(filePath));
            }
            return outFilePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return filePath;
        } finally {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 压缩到指定大小
     *
     * @param filePath 源文件文件路径
     * @param maxSize  指定大小 单位KB
     */
    public static String sampleToSize(String filePath, int maxSize) {
        maxSize *= 1024;
        if (FileUtil.getFileSize(filePath) > maxSize) {
            int quality = 100;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; //等于2的意思 等比例缩放源图长宽缩短一半
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            while (baos.toByteArray().length > maxSize) {
                baos.reset(); //每次都置空baos
                options.inSampleSize *= 2;
                Bitmap bitmap2 = BitmapFactory.decodeFile(filePath, options);
                bitmap2.compress(Bitmap.CompressFormat.JPEG, quality, baos); //进行压缩
                bitmap2.recycle();
            }
            String outFilePath = getCacheImagePath();
            LogUtil.d("=====输出的文件路径====" + outFilePath);
            try {
                FileOutputStream fos = new FileOutputStream(new File(outFilePath)); //将压缩的文件写进设置好目录文件中
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                return outFilePath;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return filePath;
            } catch (IOException e) {
                e.printStackTrace();
                return filePath;
            } finally {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return filePath;
        }
    }

    public static String qualityToSize(String path) {
        return qualityToSize(path, COMPRESS_MAX_SIZE, true);
    }

    public static String sampleToSize(String path) {
        return sampleToSize(path, COMPRESS_MAX_SIZE);
    }

    public static String blendCompress(String filePath, int maxSize) {
        if (FileUtil.getFileSize(filePath) > COMPRESS_SIZE) {
            //如果大于1M
            //先采用缩放压缩
            String sizePath = sizeCompress(filePath);
            //压缩到小于1M，在采用质量压缩
            return qualityToSize(sizePath, maxSize, false);
        } else {
            //文件小于1M的直接用质量压缩
            return qualityToSize(filePath, maxSize, true);
        }
    }

    public static void compressList(final List<Uri> pathList, final int maxSize, ICompressCallBack compressCallBack) {
        Observable.just(pathList)
                .subscribeOn(Schedulers.io())
                .map(strings -> {
                    List<String> paths = new ArrayList<>();
                    for (Uri u : strings) {
                        paths.add(blendCompress(FileUtil.getRealPath(CommonLib.getApplication(), u), maxSize));
                    }
                    return paths;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<String> strings) {
                        compressCallBack.compressSuccess(strings);
                    }

                    @Override
                    public void onError(Throwable e) {
                        compressCallBack.compressFail(e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
