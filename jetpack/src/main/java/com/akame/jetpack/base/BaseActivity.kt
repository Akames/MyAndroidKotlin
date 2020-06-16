package com.akame.jetpack.base

import android.view.View
import com.akame.developkit.baseview.BaseLibActivity
import com.akame.jetpack.R

abstract class BaseActivity : BaseLibActivity() {
    override fun getEmptyViewId(): Int = R.layout.layout_test_empty

    override fun getErrorViewId(): Int = R.layout.layout_test_err

    override fun getTitleBarView(): View? = findViewById(R.id.titleBar)

    override fun isLightStatueBar(): Boolean = true

    override fun getLoadingViewId(): Int = R.layout.layout_loading_view

    override fun showLoadingDialog() {

    }

    override fun dismissLoadingDialog() {

    }

}