package com.akame.developkit.image

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment

class ImageOptions(private val builder: Builder) {

    private val loaderManager by lazy { GliderLoaderManger() }

    fun getContext(): Any {
        return when {
            builder.context != null -> builder.context!!
            builder.activity != null -> builder.context!!
            builder.fragment != null -> builder.context!!
            else -> RuntimeException("请输入正确context对象")
        }
    }

    fun getPlaceholderRes(): Int? = builder.placeholderRes

    fun getErrorRes(): Int? = builder.errorRes

    fun getBlur(): Int? = builder.blur

    fun getEnlarge(): Int = builder.enlarge

    fun getRound(): Int? = builder.round

    fun getRoundType(): ImageCornerType? = builder.roundType

    fun getBorderWidth(): Int? = builder.borderWith

    fun getBorderColor(): Int? = builder.borderColor

    fun getBorderRound(): Int = builder.borderRound

    fun isCenterCrop(): Boolean = builder.centerCrop

    fun isCircleCrop(): Boolean = builder.circleCrop

    fun isConstrain(): Boolean = builder.constrain

    fun getImagePath(): Any = builder.url

    fun show(imageView: ImageView, imageCallBack: ImageCallBack? = null) {
        loaderManager.displayImage(this, imageView, imageCallBack)
    }

    fun cleanMemory(context: Context) {
        loaderManager.cleanMemory(context)
    }

    fun clearMemoryCache(context: Context) {
        loaderManager.clearMemoryCache(context)
    }

    fun pauseLoad(context: Context) {
        loaderManager.pauseLoad(context)
    }

    fun resumeLoad(context: Context) {
        loaderManager.resumeLoad(context)
    }

    class Builder {
        var context: Context? = null
        var activity: Activity? = null
        var fragment: Fragment? = null
        lateinit var url: Any //图片路径
        var placeholderRes: Int? = null//  加载中的图片
        var errorRes: Int? = null // 加载失败图片

        var blur: Int? = null // 模糊度  (在0.0到25.0之间)
        var enlarge: Int = 1 //放大的倍数   默认为1  表示1：1 不放大

        var round: Int? = null // 圆角度数
        var roundType: ImageCornerType? = null //圆角类型

        var borderWith: Int? = null // 边框宽度
        var borderColor: Int? = null //边框颜色
        var borderRound: Int = 0 // 边框圆角度数 默认0度 没有圆角

        var centerCrop: Boolean = false // 是否居中放大
        var circleCrop: Boolean = false //是否加载圆形

        var constrain: Boolean = false // 是否等比例加载


        fun context(context: Context): Builder {
            this.context = context
            return this
        }

        fun activity(activity: Activity): Builder {
            this.activity = activity
            return this
        }


        fun fragment(fragment: Fragment): Builder {
            this.fragment = fragment
            return this
        }


        fun placeholderRes(placeholderRes: Int): Builder {
            this.placeholderRes = placeholderRes
            return this
        }

        fun errorRes(errorRes: Int): Builder {
            this.errorRes = errorRes
            return this
        }

        fun blurAndEnlarge(blur: Int, enlarge: Int = 1): Builder {
            this.blur = blur
            this.enlarge = enlarge
            return this
        }

        fun round(round: Int, roundType: ImageCornerType? = ImageCornerType.ALL): Builder {
            this.round = round
            this.roundType = roundType
            return this
        }

        fun border(width: Int, color: Int, round: Int): Builder {
            this.borderWith = width
            this.borderColor = color
            this.borderRound = round
            return this
        }

        fun centerCrop(): Builder {
            this.centerCrop = true
            return this
        }

        fun circleCrop(): Builder {
            this.circleCrop = true
            return this
        }

        fun constrain(): Builder {
            this.constrain = true
            return this
        }

        fun url(imagePath: Any): ImageOptions {
            this.url = imagePath
            return ImageOptions(this)
        }

        fun builder(): ImageOptions = ImageOptions(this)
    }
}