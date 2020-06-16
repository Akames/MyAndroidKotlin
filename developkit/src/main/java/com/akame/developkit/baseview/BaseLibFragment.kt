package com.akame.developkit.baseview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.akame.developkit.util.StatusLayoutManger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

abstract class BaseLibFragment : Fragment(), CoroutineScope by MainScope() {
    private var statueLayout: StatusLayoutManger? = null
    protected var mContentView: View? = null
    protected var isLoadData = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mContentView == null) {
            mContentView = inflater.inflate(getLayoutResource(), container, false)
        }
        return mContentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        if (!isLoadData) {
            isLoadData = true
            lazyData()
        }
    }

    abstract fun getLayoutResource(): Int

    abstract fun init()

    abstract fun lazyData()

    open fun initListener() {}

    abstract fun getErrorViewId(): Int
    abstract fun getEmptyViewId(): Int

    fun createStatue(
        statueView: View,
        emptyId: Int = getEmptyViewId(),
        errorId: Int = getErrorViewId(),
        errorViewBuilder: StatusLayoutManger.ErrorViewBuilder? = null
    ) {
        statueLayout = StatusLayoutManger.Builder()
            .context(context!!)
            .defaultView(statueView)
            .errorViewId(errorId, errorViewBuilder)
            .emptyViewId(emptyId)
            .builder()
    }

    fun showDefaultView() {
        statueLayout?.showDefaultView()
    }

    fun showErrorView() {
        statueLayout?.showErrorView()
    }

    fun showEmptyView() {
        statueLayout?.showEmptyView()
    }

    /**
     * 显示加载中提示框
     */
    abstract fun showLoadingDialog()

    /**
     * 关闭加载中提示框
     */
    abstract fun dismissLoadingDialog()

    fun launchService(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(String) -> Unit = {},
        finalBlock: suspend CoroutineScope.() -> Unit = {}
    ) {
        launch {
            tryCatch(tryBlock, catchBlock, finalBlock)
        }
    }

    private suspend fun tryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(String) -> Unit,
        finalBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                tryBlock()
            } catch (e: Exception) {
                catchBlock(e.message!!)
            } finally {
                finalBlock()
            }
        }

    }
}