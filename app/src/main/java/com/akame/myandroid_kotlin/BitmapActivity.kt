package com.akame.myandroid_kotlin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akame.developkit.util.ScreenUtil
import kotlinx.android.synthetic.main.activity_bitmap.*


class BitmapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)
//        val op =BitmapFactory.Options().apply {
//            inJustDecodeBounds
//        }

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_bitmap)
        val bp = zoomImg(bitmap,ScreenUtil.getScreenWidth(),(ScreenUtil.getScreenWidth()*1f/bitmap.width*bitmap.height).toInt())
        iv_bitmap.setImageBitmap(bp)
        bitmap.recycle()
        tv_data.text = "图片日志信息: 宽：${bp?.width} 高：${bp?.height}"

    }

    override fun onResume() {
        super.onResume()
    }

    private fun zoomImg(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        // 获得图片的宽高
        val width = bm.width
        val height = bm.height
        // 计算缩放比例
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
    }

}