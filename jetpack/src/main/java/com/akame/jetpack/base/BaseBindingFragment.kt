package com.akame.jetpack.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akame.developkit.BaseApp
import com.akame.developkit.showMsg
import com.akame.developkit.viewmodel.BaseViewModel

abstract class BaseBindingFragment<T : ViewDataBinding, VM : BaseViewModel> : BaseFragment() {
    protected lateinit var dataBinding: T
    protected val viewModel: VM by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(BaseApp.app).create(getViewModule())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false)
        dataBinding.lifecycleOwner = this
        mContentView = dataBinding.root
        viewModel.toastMsg.observe(viewLifecycleOwner, Observer { showMsg(it) })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun getViewModule(): Class<VM>
}