package com.akame.developkit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.akame.developkit.image.ImageLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import org.jetbrains.annotations.NotNull

fun Activity.showMsg(msg: Any) {
    Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show()
}

fun Fragment.showMsg(msg: Any) {
    Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()
}

fun Any.showMsg(context: Context, msg: Any) {
    Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()
}

fun Any.showLog(msg: Any) {
    Log.d(this.javaClass.simpleName, "---logMsg---: $msg")
}

fun Activity.newIndexIntent(cls: Class<*>): Intent = Intent(this, cls)

@BindingAdapter("app:imageUrl")
fun ImageView.disPlayImage(@NotNull url: Any) {
    ImageLoader.with(this.context)
        .constrain()
        .centerCrop()
//        .border(2, Color.RED, 16)
        .url(url)
        .show(this)
}

fun ImageView.disPlay(url: Any) {
    ImageLoader.with(this.context)
        .url(url)
        .show(this)
}


var itemTime = 0L
fun View.setOnClickListen(method: () -> Unit) {
    setOnClickListener {
        if (System.currentTimeMillis() - itemTime > 1000) {
            itemTime = System.currentTimeMillis()
            method.invoke()
        }
    }
}





