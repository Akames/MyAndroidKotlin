package com.akame.developkit

import android.app.Application

object BaseApp {
    lateinit var app: Application
    fun attachApp(application: Application) {
        app = application
    }
}