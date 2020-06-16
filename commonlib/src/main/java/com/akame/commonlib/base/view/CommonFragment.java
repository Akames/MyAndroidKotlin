package com.akame.commonlib.base.view;

import android.view.View;

import com.akame.commonlib.abs.IStatusLayoutHelp;
import com.akame.commonlib.abs.IViewListener;
import com.akame.commonlib.base.presenter.BasePresenter;
import com.akame.commonlib.utils.LoadingDialog;
import com.akame.commonlib.utils.StatusLayoutUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * @Author: Akame
 * @Date: 2019/2/15
 * @Description: 在baseLibFragment基础上分发基础的业务
 */
public abstract class CommonFragment<T extends BasePresenter> extends BaseLibFragment<T> {
    private SmartRefreshLayout smartRefreshLayout;
    private LoadingDialog loadingDialog;
    private boolean isFirstLoader = true; //是否是第一次加载
    private boolean isPrepare = false; //是否准备好，页面初始化完毕
    private IStatusLayoutHelp layoutUtil;

    @Override
    protected void initData() {
        isPrepare = true;
    }

    @Override
    public SmartRefreshLayout getRefreshControl() {
        return smartRefreshLayout;
    }

    @Override
    public void setRefreshControl(SmartRefreshLayout smartRefreshLayout) {
        this.smartRefreshLayout = smartRefreshLayout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirstLoader && isPrepare && isVisibleToUser) {
            lazyLoadData();
            isFirstLoader = false;
        }
    }

    /* 创建页面提示**/
    public void createLayoutHintView(View currView, final IViewListener.ViewClickListener clickListen) {
        layoutUtil = StatusLayoutUtil.configStatusView(mActivity, currView, clickListen);
    }

    @Override
    public void showEmptyView() {
        if (layoutUtil != null)
            layoutUtil.showEmptyView();
    }

    @Override
    public void showNetErrorView() {
        if (layoutUtil != null)
            layoutUtil.showNetErrorView();
    }

    @Override
    public void showDefaultLayout() {
        if (layoutUtil != null)
            layoutUtil.showDefaultView();
    }

    @Override
    public void showLoadingView() {
        if (layoutUtil != null) {
            layoutUtil.showLoadingView();
        }
    }

    @Override
    public void hideLoadingView() {
        if (layoutUtil != null) {
            layoutUtil.hideLoadingView();
        }
    }

    @Override
    public void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(mActivity);
        }
        loadingDialog.show();
    }

    @Override
    public void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (loadingDialog != null) {
            loadingDialog.cancel();
            loadingDialog = null;
        }
        super.onDestroy();
    }

    protected void lazyLoadData() {
    }
}
