package com.akame.videoplay

import android.app.Application

object VideoPlayApp {
    lateinit var app: Application

    fun attachApp(app: Application) {
        this.app = app
    }
}