package com.akame.jetpack.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akame.developkit.BaseApp
import com.akame.developkit.showMsg
import com.akame.developkit.viewmodel.BaseViewModel

abstract class BaseBindingActivity<T : ViewDataBinding, VM : BaseViewModel> :
    BaseActivity() {
    protected lateinit var dataBinding: T
    protected val viewModel: VM by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(BaseApp.app).create(getViewModule())
    }

    override fun isBinding(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        dataBinding = DataBindingUtil.setContentView(this, getLayoutResource())
        dataBinding.lifecycleOwner = this
        super.onCreate(savedInstanceState)
        viewModel.toastMsg.observe(this, Observer { showMsg(it) })
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBinding.unbind()
    }

    abstract fun getViewModule(): Class<VM>
}