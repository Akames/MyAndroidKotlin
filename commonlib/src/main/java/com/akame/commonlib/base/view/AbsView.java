package com.akame.commonlib.base.view;

import com.akame.commonlib.abs.IPermissionsResultListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * @Author: Restring
 * @Date: 2018/4/23
 * @Description:
 */
public interface AbsView {
    /**
     * Show error message
     *
     * @param errorMsg error message
     */
    void showMsg(String errorMsg);

    /**
     * Show loading
     */
    void showLoadingView();

    /**
     * dis loading
     */
    void hideLoadingView();

    /**
     * 网络错误
     */
    void showNetErrorView();

    /***
     * 空数据
     */
    void showEmptyView();

    /**
     * 默认数据页面
     */
    void showDefaultLayout();

    /**
     * 权限请求
     */
    void requestPermission(String[] permission, IPermissionsResultListener listener);

    /**
     * 获取刷新控件
     *
     * @return
     */
    SmartRefreshLayout getRefreshControl();

    /**
     * 设置刷新控件
     */
    void setRefreshControl(SmartRefreshLayout smartRefreshLayout);

    /**
     * 显示加载提示Dialog
     */
    void showLoadingDialog();

    /**
     * 隐藏加载提示Dialog
     */
    void hideLoadingDialog();
}
