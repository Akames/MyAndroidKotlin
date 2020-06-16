package com.akame.commonlib.abs;

/**
 * @Author: Restring
 * @Date: 2018/5/9
 * @Description:
 */
public interface IStatusLayoutHelp {
    /***
     * 空数据
     */
    void showEmptyView();

    /**
     * 原始页面
     */
    void showDefaultView();

    /**
     * 网络错误
     */
    void showNetErrorView();

    /**
     * 显示加载提示框
     */
    void showLoadingView();

    /**
     * 隐藏加载提示框
     */
    void hideLoadingView();
}
