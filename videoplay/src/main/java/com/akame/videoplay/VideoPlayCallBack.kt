package com.akame.videoplay

interface VideoPlayCallBack {
    fun onPreparedListener()
    fun onCompletionListener()
    fun onErrorListener(errorCode: Int)
    fun onReplayClickListener()
    fun onNextPlayClickListener()
    fun onShareClickListener()
    fun onStart()
    fun onPause()
}