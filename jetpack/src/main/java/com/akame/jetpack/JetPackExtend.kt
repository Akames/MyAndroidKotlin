package com.akame.jetpack

import android.widget.Toast

fun showMsg(
    msg: String
) {
    Toast.makeText(JetPackApp.app, msg, Toast.LENGTH_SHORT).show()
}