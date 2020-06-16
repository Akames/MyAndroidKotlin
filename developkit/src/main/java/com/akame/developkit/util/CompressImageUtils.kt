package com.akame.developkit.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object CompressImageUtils {
    /**
     * 质量压缩 修改图片的清晰度进行压缩 图片的长宽不变
     */
    fun qualityToSize(filePath: String, currentSize: Int, outFilePath: String) {
        var quality = 100
        val size = currentSize * 1024
        //将要压缩的图片转化为bitmap
        val bitmap = BitmapFactory.decodeFile(filePath)
        val baos = ByteArrayOutputStream()
        //将bitmap写入到输出流
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        while (baos.toByteArray().size > size) {
            baos.reset()
            quality -= 10
            if (quality < 0) {
                break
            }
            //将新的图片格式写入到输入流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        }
        //将压缩好的输入流保存到指定文件
        try {
            val fos = FileOutputStream(File(outFilePath))
            fos.write(baos.toByteArray())
            fos.flush()
            fos.close()
            baos.close()
        } catch (e: Exception) {

        } finally {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }

    /**
     * 采样率压缩  压缩速度要高于质量压缩
     */
    fun samplingToSize(filePath: String, currentSize: Int, outFilePath: String) {
        val size = currentSize * 1024
        val quality = 100
        val options = BitmapFactory.Options()
        options.inSampleSize = 2
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        while (baos.toByteArray().size > size) {
            baos.reset()
            options.inSampleSize *= 2
            val bp = BitmapFactory.decodeFile(filePath, options)
            bp.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            bp.recycle()
        }
        try {
            val fos = FileOutputStream(File(outFilePath))
            fos.write(baos.toByteArray())
            fos.flush()
            fos.close()
            baos.close()
        } catch (e: Exception) {

        } finally {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }


}