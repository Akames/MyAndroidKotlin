package com.akame.videoplay.unit

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.akame.videoplay.VideoPlayApp

fun Any.showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun Any.showLog(msg: String) {
    Log.e(this.javaClass.simpleName, msg)
}

fun isNetworkAvailable(): Boolean {
    val manager = VideoPlayApp.app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netWorkInfo = manager.activeNetworkInfo
    return netWorkInfo?.isConnected ?: false
}