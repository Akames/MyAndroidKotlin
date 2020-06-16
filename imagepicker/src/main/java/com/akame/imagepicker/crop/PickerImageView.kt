package com.akame.imagepicker.crop

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import com.akame.developkit.util.FileUtil
import com.akame.developkit.util.ScreenUtil
import com.akame.imagepicker.ImageUtil
import java.io.File
import java.io.FileOutputStream
import java.lang.Math.max
import java.lang.Math.min


class PickerImageView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    //要绘制的图片
    private lateinit var bitmap: Bitmap
    //绘制的图片的画笔
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG) //抗锯齿
    //图片缩放比例
    private var mScale = 1f
    //图片旋转角度
    private var dagger = 0
    //图片位置坐标
    private var mTranslationY = 0f
    private var mTranslationX = 0f

    //监听手势
    private var detector: GestureDetectorCompat
    //缩放手势
    private var scaleDetector: ScaleGestureDetector
    //滑动
    private val scroll by lazy {
        OverScroller(context)
    }
    //最小放大系数
    var smallScale = 0f
    //最大放大系数
    var bigScale = 0f
    //上一次放大系数 记录每次放大系数 可以让图片缩放保持连贯性
    private var oldScale = 0f
    //裁剪区域大小
    private val displaySize: Float by lazy {
        width - ScreenUtil.dip2px(16f) * 2f
    }

    init {
        paint.isFilterBitmap = true
        paint.isDither = true
        detector =
            GestureDetectorCompat(getContext(), object : GestureDetector.SimpleOnGestureListener() {
                var maxX = 0f
                var minX = 0f
                var maxY = 0f
                var minY = 0f

                override fun onDown(e: MotionEvent?): Boolean {
                    //计算出放大后的图片距离显示区域的距离 然后除以2 ，
                    maxX = (bitmap.width * mScale - displaySize) / 2
                    minX = -maxX
                    maxY = ((bitmap.height * mScale) - displaySize) / 2
                    minY = -maxY
                    return true
                }

                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    //双击屏幕
                    val animator = ValueAnimator.ofFloat(
                        mScale, if (mScale == smallScale)
                            (smallScale + 0.5f)
                        else smallScale
                    )
                    animator.addUpdateListener {
                        mScale = it.animatedValue as Float
                        invalidate()
                    }
                    animator.interpolator = LinearInterpolator()
                    animator.duration = 500
                    animator.start()
                    return super.onDoubleTap(e)
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    mTranslationX -= distanceX
                    mTranslationY -= distanceY
                    invalidate()
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }

                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    scroll.fling(
                        mTranslationX.toInt(), mTranslationY.toInt(),
                        velocityX.toInt(), velocityY.toInt(),
                        minX.toInt(), maxX.toInt(),
                        minY.toInt(), maxY.toInt()
                    )

                    ViewCompat.postOnAnimation(this@PickerImageView, object : Runnable {
                        override fun run() {
                            if (scroll.computeScrollOffset()) {
                                mTranslationX = scroll.currX.toFloat()
                                mTranslationY = scroll.currY.toFloat()
                                invalidate()
                                ViewCompat.postOnAnimation(this@PickerImageView, this)
                            }
                        }
                    })
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })

        scaleDetector = ScaleGestureDetector(context, object :
            ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                oldScale = mScale
                return true
            }

            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                mScale = detector?.scaleFactor!! * oldScale
                mScale = max(mScale, smallScale)
                mScale = min(mScale, bigScale)
                invalidate()
                return super.onScale(detector)
            }

        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //判断图片是否是竖图，根据长-宽 <0就是竖图 或者角度为90的也是 因为旋转后就是竖图了
        mScale = if (bitmap.width - bitmap.height <= 0 || dagger == 90) {
            //是竖图
            displaySize / (bitmap.width * 1f)
        } else {
            //是横图
            displaySize / (bitmap.height * 1f)
        }
        smallScale = mScale //最小放大系数就是默认放大系数
        bigScale = smallScale + 3 // 最大放大系数就是最小的基础上加3 一般3就比较大了
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        checkPosition()
        canvas?.translate(mTranslationX, mTranslationY) //移动到中心
        canvas?.scale(mScale, mScale, width / 2f, height / 2f)//x轴y轴缩放
        canvas?.drawBitmap(
            bitmap,
            (width - bitmap.width) / 2f,
            (height - bitmap.height) / 2f,
            paint
        ) //绘制图片
    }

    /**
     * 检查当前图片移动的位置是否越界
     */
    private fun checkPosition() {
        val maxX = (bitmap.width * mScale - displaySize) / 2
        val minX = -maxX
        val maxY = ((bitmap.height * mScale) - displaySize) / 2
        val minY = -maxY

        mTranslationX = min(mTranslationX, maxX)
        mTranslationX = max(mTranslationX, minX)

        mTranslationY = min(mTranslationY, maxY)
        mTranslationY = max(mTranslationY, minY)
    }

    /**
     * 触摸分发
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.pointerCount == 2) {
            return scaleDetector.onTouchEvent(event)
        }
        return detector.onTouchEvent(event)
    }


    /**
     * 设置图片资源
     */
    fun setImageResource(imageResource: String) {
        //系统拍照到照片可能旋转，故先判断读取图片的角度，然后进行图片旋转
        dagger = ImageUtil.getImageDegree(imageResource)
        bitmap = BitmapFactory.decodeFile(imageResource)
        if (dagger != 0) {
            bitmap = ImageUtil.rotateToDegree(bitmap, dagger.toFloat())
        }
    }


    /**
     * 裁剪图片
     */
    fun corpImage(rect: RectF): String {
        val bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        draw(canvas)

        val bp = Bitmap.createBitmap(
            bitmap,
            rect.left.toInt(),
            rect.top.toInt(),
            displaySize.toInt(),
            displaySize.toInt()
        )
        return saveBitmap(bp)
    }

    /**
     * 将bitmap保存到本地
     */
    private fun saveBitmap(bitmap: Bitmap): String {
        val file =
            File(FileUtil.getCachePath(), System.currentTimeMillis().toString() + ".jpg")
        val out = FileOutputStream(file)
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        } catch (e: Exception) {

        } finally {
            out.flush()
            out.close()
        }
        return file.absolutePath
    }

}
