package com.akame.developkit.image

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

object ImageLoader {
    fun with(context: Context): ImageOptions.Builder {
        return ImageOptions.Builder().apply {
            this.context = context
        }
    }

    fun with(context: Activity): ImageOptions.Builder {
        return ImageOptions.Builder().apply {
            this.activity = context
        }
    }

    fun with(context: Fragment): ImageOptions.Builder {
        return ImageOptions.Builder().apply {
            this.fragment = context
        }
    }

    fun paseLoad(context: Context) {
        ImageOptions.Builder().builder().pauseLoad(context)
    }

    fun resumeLoad(context: Context) {
        ImageOptions.Builder().builder().resumeLoad(context)
    }

    fun cleanMemory(context: Context) {
        ImageOptions.Builder().builder().cleanMemory(context)
    }

    fun clearMemoryCache(context: Context) {
        ImageOptions.Builder().builder().clearMemoryCache(context)
    }
}