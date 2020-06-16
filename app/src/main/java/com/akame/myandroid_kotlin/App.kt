package com.akame.myandroid_kotlin

import android.app.Application
import com.akame.jetpack.JetPackApp
import com.akame.videoplay.VideoPlayApp

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        JetPackApp.attachApp(this)
        VideoPlayApp.attachApp(this)
        instance = this
    }

    companion object {
        var instance: Application? = null
    }


}