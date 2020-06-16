package com.akame.jetpack

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView

class MyImageView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet) {

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.isFilterBitmap = true
        paint.isDither = true
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        if (drawable is BitmapDrawable) {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val b = getSrcBitmap(bitmap)
            paint.reset()
            canvas?.drawBitmap(b, 0f, 0f, paint)
        }
    }

    fun getSrcBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        ) //实例化一个bitmap容器
        val canvas = Canvas(output) // 通过canvas把真实的bitmap写入进去
        canvas.drawARGB(0, 0, 0, 0)
//        canvas.drawCircle(width / 2f, height / 2f, width / 2f, paint)
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(0f, height / 2f)
        path.lineTo(width / 2f, height/4f)
        path.lineTo(width.toFloat(), height / 2f)
        path.lineTo(width.toFloat(),0f)
        path.close()
        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }
}
