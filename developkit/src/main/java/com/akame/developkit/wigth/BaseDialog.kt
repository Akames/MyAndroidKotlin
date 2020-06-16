package com.akame.developkit.wigth

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import kotlinx.coroutines.*

abstract class BaseDialog(context: Context) : Dialog(context) {
    private val jobList: ArrayList<Job> by lazy { arrayListOf<Job>() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getDialogLayoutRes())
        setWindowSize(getWindowWidth(), getWindowHeight())
        init()
    }

    abstract fun getDialogLayoutRes(): Int

    abstract fun init()

    private fun setWindowSize(width: Int, height: Int) {
        val layoutParams = window?.attributes
        window?.setGravity(Gravity.CENTER)
        layoutParams?.width = width
        layoutParams?.height = height
        window?.attributes = layoutParams
    }

    open fun getWindowWidth(): Int = WindowManager.LayoutParams.MATCH_PARENT

    open fun getWindowHeight(): Int = WindowManager.LayoutParams.WRAP_CONTENT

     fun launch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(Throwable) -> Unit = {},
        finalBlock: suspend CoroutineScope.() -> Unit = {}
    ) {
        jobList.add(GlobalScope.launch {
            coroutineScope {
                try {
                    tryBlock()
                } catch (e: Exception) {
                    catchBlock(e)
                } finally {
                    finalBlock()
                }
            }
        })
    }

    override fun dismiss() {
        super.dismiss()
        jobList.forEach {
            if (!it.isCancelled) {
                it.cancel()
            }
        }
    }
}