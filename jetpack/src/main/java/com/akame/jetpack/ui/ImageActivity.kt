package com.akame.jetpack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akame.developkit.disPlay
import com.akame.jetpack.R
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        iv_image.disPlay(intent.getStringExtra("url"))
    }
}
