package com.akame.videoplay.unit

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    var HH_mm_hh = "HH:mm:ss"
    var mm_hh = "mm:ss"

    fun formatData(date: Long): String {
        val format: String = when (date) {
            in 0..3599999 -> {
                mm_hh
            }
            else -> {
                HH_mm_hh
            }
        }
        return SimpleDateFormat(format, Locale.CHINESE).apply {
            this.isLenient =false
        }.format(date-TimeZone.getDefault().rawOffset)
    }
}