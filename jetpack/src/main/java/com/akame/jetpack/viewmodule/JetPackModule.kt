package com.akame.jetpack.viewmodule

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.akame.developkit.viewmodel.BaseViewModel
import com.akame.jetpack.JetPackComment.requestServer
import com.akame.jetpack.data.RepositoryManger
import com.akame.jetpack.data.net.been.ImageViewBeen

class JetPackModule : BaseViewModel() {
    var title = MutableLiveData<String>()

    // val test = mutableListOf<ImageViewBeen>() 这里可以优化一下
    val items = MutableLiveData<ArrayList<ImageViewBeen>>().apply {
        this.value = ArrayList()
    }
    var netWorkStatue = MutableLiveData<Int>()

    fun getTitle() {
        title.value = "今天没有☔️"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        getImageList()
    }

    fun getImageList() {
        netWorkStatue.value = 0
        requestServer(requestServer = {
            RepositoryManger.service.getImageListAsync()
        }, success = {
            items.value = it
//            title.value = "数据获取成功！"
        }, fail = {
//            title.value = "数据请求失败！"
            error.value = "数据请求失败"
        }, complete = {
            netWorkStatue.value = 1
        })

    }
}