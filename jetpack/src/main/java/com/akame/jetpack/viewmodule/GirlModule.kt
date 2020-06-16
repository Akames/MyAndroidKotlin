package com.akame.jetpack.viewmodule

import androidx.lifecycle.MutableLiveData
import com.akame.developkit.showLog
import com.akame.developkit.viewmodel.BaseViewModel
import com.akame.jetpack.JetPackComment.requestServer
import com.akame.jetpack.data.RepositoryManger
import com.akame.jetpack.data.net.been.GirlBeen

class GirlModule : BaseViewModel() {

    val items : MutableLiveData<ArrayList<GirlBeen>> by lazy {
        MutableLiveData<ArrayList<GirlBeen>>().apply { this.value = ArrayList() }
    }
    var pager = 0
    fun getData(){
        requestServer({
            RepositoryManger.service.getGirlList(++pager)
        },{
            items.value = it
        },{
            showLog("----数据错误--$it---")
        })
    }
}