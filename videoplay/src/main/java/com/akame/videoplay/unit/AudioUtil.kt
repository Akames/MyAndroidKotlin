package com.akame.videoplay.unit

import android.content.Context
import android.media.AudioManager
import android.util.Log
import kotlin.math.max
import kotlin.math.min

class AudioUtil(context: Context) {
    private val audioManger: AudioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    //最大音量
    private fun getMaxVolume() = audioManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    //当前音量
    fun getCurrentVolume() = audioManger.getStreamVolume(AudioManager.STREAM_MUSIC)

    /**
     * 获取当前音量的百分比
     */

    private fun getCurrentVolumePercent() =
        (getCurrentVolume() / (getMaxVolume() * 1f) * 100).toInt()

    /**
     * 设置音量
     */
    private fun setStreamVolume(value: Int) {
        audioManger.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            value,
            0 //表示不显示UI
        )
    }

    fun setVolumePercent(percent: Float, defaultVolume: Int): Int {
        var volume = (defaultVolume + percent * getMaxVolume()).toInt()
        volume = min(volume, getMaxVolume())
        volume = max(volume, 0)
        setStreamVolume(volume)
        return getCurrentVolumePercent()
    }
}