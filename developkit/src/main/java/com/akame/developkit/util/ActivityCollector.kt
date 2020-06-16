package com.akame.developkit.util

import android.app.Activity
import kotlin.system.exitProcess

object ActivityCollector {
    private val activitySet = HashSet<Activity>()

    fun addActivity(activity: Activity) {
        activitySet.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activitySet.remove(activity)
    }

    fun removeAllActivity() {
        synchronized(activitySet) {
            activitySet.forEach {
                it.finish()
            }
        }
    }

    fun exitApp() {
        removeAllActivity()
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(0)
    }
}