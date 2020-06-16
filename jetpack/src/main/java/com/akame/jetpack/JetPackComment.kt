package com.akame.jetpack

import com.akame.developkit.viewmodel.BaseViewModel
import com.akame.jetpack.base.BaseActivity
import com.akame.jetpack.base.BaseFragment
import com.akame.jetpack.data.net.been.ServerResult
import kotlinx.coroutines.*

object JetPackComment {
    /**
     * 分析后端接口 一般根据返回code来判断当前请求返回的数据是否正常
     * 根据不同的code码进行不同的逻辑判断
     */
    suspend fun <T> executeResponse(
        response: ServerResult<T>,
        successBlock: suspend CoroutineScope.() -> Unit,
        errorBlock: suspend CoroutineScope.(errorMsg: String) -> Unit = {}
    ) {
        coroutineScope {
            if (response.status != 100) {
                errorBlock(response.data.toString())
            } else {
                successBlock()
            }
        }
    }

    /**
     * 进行网络请求
     * @param requestServer  请求对象 例如retrofit.service 结果返回的是ServerResult 可配
     * @param success 网络请求成功。这个成功仅仅代表网络请求后台有响应了，并不代表接口逻辑真正的的成功，如果遇到断网 或者参数错误 会走下面的fail
     * @param fail 网络请求失败，比如断网 参数错误，地址错误等等
     * @param complete 请求完成 不论成功和失败 最终都会回调用 用来处理一些资源回收等 可以不传
     */
    fun <T> BaseViewModel.requestServer(
        requestServer: suspend CoroutineScope.() -> ServerResult<T>,
        success: (T) -> Unit,
        fail: ((String) -> Unit)? = null,
        complete: (() -> Unit)? = null
    ) {
        launchService(tryBlock = {
            val data = withContext(Dispatchers.IO) {
                //进行网络请求 返回请求数据
                requestServer()
            }
            //分析后端返回的数据是否正在的请求成功
            executeResponse(
                response = data,
                successBlock = { success(data.data) },
                errorBlock = { fail?.invoke(it) })
        },
            catchBlock = { fail?.invoke(it) },
            finalBlock = { complete?.invoke() })
    }


    fun <T> BaseActivity.requestServer(
        requestServer: suspend CoroutineScope.() -> ServerResult<T>,
        success: (T) -> Unit,
        fail: (String) -> Unit,
        complete: (() -> Unit)? = null
    ) {
        launchService(tryBlock = {
            val data = withContext(Dispatchers.IO) {
                //进行网络请求 返回请求数据
                requestServer()
            }
            //分析后端返回的数据是否正在的请求成功
            executeResponse(
                response = data,
                successBlock = { success(data.data) },
                errorBlock = { fail(it) })
        },
            catchBlock = { fail(it) },
            finalBlock = { complete?.invoke() })
    }


    fun <T> BaseFragment.requestServer(
        requestServer: suspend CoroutineScope.() -> ServerResult<T>,
        success: (T) -> Unit,
        fail: (String) -> Unit,
        complete: (() -> Unit)? = null
    ) {
        launchService(tryBlock = {
            val data = withContext(Dispatchers.IO) {
                //进行网络请求 返回请求数据
                requestServer()
            }
            //分析后端返回的数据是否正在的请求成功
            executeResponse(
                response = data,
                successBlock = { success(data.data) },
                errorBlock = { fail(it) })
        },
            catchBlock = { fail(it) },
            finalBlock = { complete?.invoke() })
    }

}