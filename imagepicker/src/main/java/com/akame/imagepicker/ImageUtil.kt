package com.akame.imagepicker

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface

object ImageUtil {

    /**
     * 读取一张图片的旋转角度
     */
    fun getImageDegree(imagePath: String): Int {
        val exifInterface = ExifInterface(imagePath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    /**
     * 将图片旋转到指定角度
     */
    fun rotateToDegree(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.reset()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}