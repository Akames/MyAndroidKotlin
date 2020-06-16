package com.akame.imagepicker.wigth

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import java.text.DecimalFormat
import kotlin.math.roundToInt


class PreviewImageView(context: Context, attributeSet: AttributeSet? = null) :
    ImageView(context, attributeSet),
    ViewTreeObserver.OnGlobalLayoutListener {
    private var isOne = true //是否是第一次加载
    private var previewMatrix = Matrix()
    private var gestureDetector: GestureDetectorCompat
    private var bigScale = 0f
    private var smallScale = 0f
    private var scaleGestureDetector: ScaleGestureDetector
    private val scroller: OverScroller by lazy { OverScroller(getContext()) }
    private var currentX = 0f
    private var currentY = 0f
    private var dft = DecimalFormat("#.00")
    private var oldScale = 0f

    init {
        scaleType = ScaleType.MATRIX
        gestureDetector =
            GestureDetectorCompat(getContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent?): Boolean {
                    return true
                }

                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    val animator = ValueAnimator.ofFloat(
                        getScale(),
                        if (dft.format(getScale()) != dft.format(smallScale)) smallScale else smallScale + 0.5f
                    )
                    animator.addUpdateListener {
                        val scale = it.animatedValue as Float / getScale()
                        previewMatrix.postScale(scale, scale, e?.x!!, e.y)
                        imageMatrix = previewMatrix
                        removeBorderAndTranslationCenter()
                    }
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
                    previewMatrix.postTranslate(-distanceX, -distanceY)
                    imageMatrix = previewMatrix
                    removeBorderAndTranslationCenter()
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    currentX = e2?.x!!
                    currentY = e2.y
                    val rectF = getMatrixRectF()
                    val minX = rectF.left.toInt()
                    val maxX = rectF.right.toInt()
                    val minY = rectF.top.toInt()
                    val maxY = rectF.bottom.toInt()

                    scroller.fling(
                        currentX.roundToInt(),
                        currentY.roundToInt(),
                        velocityX.roundToInt(),
                        velocityY.roundToInt(),
                        minX,
                        maxX,
                        minY,
                        maxY
                    )

                    ViewCompat.postOnAnimation(this@PreviewImageView, object : Runnable {
                        override fun run() {
                            if (scroller.computeScrollOffset()) {
                                val newX = scroller.currX
                                val dx = newX - currentX
                                currentX = newX.toFloat()
                                //获得当前的y坐标
                                val newY = scroller.currY
                                val dy = newY - currentY
                                currentY = newY.toFloat()
                                //进行平移操作
                                if (dx != 0f && dy != 0f) {
                                    previewMatrix.postTranslate(dx, dy)
                                    imageMatrix = previewMatrix;
                                    //去除移动边界
                                    removeBorderAndTranslationCenter()
                                }
                                ViewCompat.postOnAnimation(this@PreviewImageView, this)
                            }
                        }
                    })
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })
        scaleGestureDetector = ScaleGestureDetector(getContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                    oldScale = getScale()
                    return true
                }

                override fun onScale(detector: ScaleGestureDetector?): Boolean {
                    val scale = getScale()
                    var scaleFactor = detector?.scaleFactor!!
                    scaleFactor = scaleFactor * oldScale / getScale() // 数学没学好 代码泪两行
                    if (scale < bigScale && scaleFactor >= 1.0f || scale > smallScale && scaleFactor <= 1.0f) {
                        if (scale * scaleFactor > bigScale) {
                            scaleFactor = bigScale / scale
                        }
                        if (scale * scaleFactor < smallScale) {
                            scaleFactor = smallScale / scale
                        }
                        previewMatrix.postScale(
                            scaleFactor,
                            scaleFactor,
                            detector.focusX,
                            detector.focusY
                        )
                        imageMatrix = previewMatrix
                        removeBorderAndTranslationCenter()

                    }
                    return super.onScale(detector)
                }
            })

    }

    override fun onGlobalLayout() {
        if (isOne && drawable != null) {
            val scale = width / (drawable.intrinsicWidth * 1f)
            previewMatrix.reset()
            previewMatrix.postTranslate(
                (width - drawable.intrinsicWidth) / 2f,
                (height - drawable.intrinsicHeight) / 2f
            )
            previewMatrix.postScale(scale, scale, width / 2f, height / 2f)
            imageMatrix = previewMatrix
            smallScale = getScale()
            bigScale = smallScale + 2
            isOne = false
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
        viewTreeObserver.removeGlobalOnLayoutListener(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event) or scaleGestureDetector.onTouchEvent(event)
    }

    /**
     * 获取缩放值
     */
    private fun getScale(): Float {
        val vales = FloatArray(9)
        previewMatrix.getValues(vales)
        return vales[Matrix.MSCALE_X]
    }

    /**
     * 获取图片上下左右边界
     */
    private fun getMatrixRectF(): RectF {
        val rectF =
            RectF(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
        imageMatrix.mapRect(rectF)
        return rectF
    }

    /**
     * 防止图片越界
     */
    private fun removeBorderAndTranslationCenter() {
        val rectF = getMatrixRectF()
        val width = width
        val height = height
        val widthF = rectF.width()
        val heightF = rectF.height()
        val left = rectF.left
        val right = rectF.right
        val top = rectF.top
        val bottom = rectF.bottom
        var translationX = 0.0f
        var translationY = 0.0f

        if (left > 0) {
            //左边有边界
            if (widthF > width) {
                //图片宽度大于控件宽度，移动到左边贴边
                translationX = -left
            } else {
                //图片宽度小于控件宽度，移动到中间
                translationX = width * 1.0f / 2f - (widthF * 1.0f / 2f + left)
            }
        } else if (right < width) {
            //右边有边界
            translationX = if (widthF > width) {
                //图片宽度大于控件宽度，移动到右边贴边
                width - right
            } else {
                //图片宽度小于控件宽度，移动到中间
                width * 1.0f / 2f - (widthF * 1.0f / 2f + left)
            }
        }

        if (top > 0) {
            //顶部有边界
            translationY = if (heightF > height) {
                //图片高度大于控件高度，去除顶部边界
                -top
            } else {
                //图片高度小于控件宽度，移动到中间
                height * 1.0f / 2f - (top + heightF * 1.0f / 2f)
            }
        } else if (bottom < height) {
            //底部有边界
            translationY = if (heightF > height) {
                //图片高度大于控件高度，去除顶部边界
                height - bottom
            } else {
                //图片高度小于控件宽度，移动到中间
                height * 1.0f / 2f - (top + heightF * 1.0f / 2f)
            }
        }
        previewMatrix.postTranslate(translationX, translationY)
        imageMatrix = previewMatrix
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        val rect = getMatrixRectF()
        if (rect.isEmpty)
            return false
        return if (direction > 0) {
            rect.right >= width + 1
        } else {
            rect.left <= 0 - 1
        }
    }

    override fun canScrollVertically(direction: Int): Boolean {
        val rect = getMatrixRectF()
        if (rect.isEmpty)
            return false
        return if (direction > 0) {
            rect.bottom >= height + 1
        } else {
            rect.top <= 0 - 1
        }
    }
}