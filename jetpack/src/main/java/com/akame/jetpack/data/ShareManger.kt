package com.akame.jetpack.data

import android.content.Context
import android.content.SharedPreferences
import com.akame.jetpack.JetPackApp

class ShareManger {
    val spf: SharedPreferences  by lazy {
        JetPackApp.app.getSharedPreferences(
            "JET_PACK_DATA",
            Context.MODE_PRIVATE
        )
    }

    fun setToken(token: String) {
        spf.edit().putString("token", token).apply()
    }

    fun getToken() = spf.getString("token", "")!!

}