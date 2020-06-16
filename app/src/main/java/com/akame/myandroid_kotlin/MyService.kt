package com.akame.myandroid_kotlin

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService : Service() {
    class MyBinder : Book.Stub() {
        override fun name(): String = "我是服务端提供端数据"
    }

    val binder = MyBinder()


    override fun onBind(intent: Intent?): IBinder? = binder

}