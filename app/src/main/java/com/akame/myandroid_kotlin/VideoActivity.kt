package com.akame.myandroid_kotlin

import com.akame.jetpack.base.BaseActivity
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : BaseActivity() {
    override fun getLayoutResource(): Int = R.layout.activity_video

    override fun init() {
        val videoUrl =
            "https://jdvod.300hu.com/4c1f7a6atransbjngwcloud1oss/69758ed0111525506298941441/v.f30.mp4"
        playView.setUp(videoUrl, "LG UL950显示器")
        playView.goneShareView()

    }

    override fun onPause() {
        super.onPause()
        playView.playPause()
    }

    override fun onRestart() {
        super.onRestart()
        playView.playStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        playView.cancel()
    }
}
