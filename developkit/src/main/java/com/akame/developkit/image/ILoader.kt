package com.akame.developkit.image

import android.content.Context
import android.widget.ImageView

interface ILoader {
    fun displayImage(
        options: ImageOptions,
        imageView: ImageView,
        imageCallBack: ImageCallBack? = null
    )

    fun pauseLoad(context: Context)

    fun resumeLoad(context: Context)

    fun clearMemoryCache(context: Context)

    fun cleanMemory(context: Context)
}