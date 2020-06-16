package com.akame.jetpack

import android.app.Application
import com.akame.developkit.BaseApp

class JetPackApp {

    companion object {
        lateinit var app: Application

        fun attachApp(application: Application) {
            app = application
            BaseApp.attachApp(application)
        }
    }
}