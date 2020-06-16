package com.akame.jetpack.base

import com.akame.developkit.baseview.BaseLibFragment
import com.akame.jetpack.R

abstract class BaseFragment : BaseLibFragment() {
    override fun getEmptyViewId(): Int = R.layout.layout_test_empty
    override fun getErrorViewId(): Int = R.layout.layout_test_err
    override fun showLoadingDialog() {

    }

    override fun dismissLoadingDialog() {

    }
}