package com.akame.commonlib.base.view;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.akame.commonlib.CommonLib;
import com.akame.commonlib.R;
import com.akame.commonlib.abs.IStatusLayoutHelp;
import com.akame.commonlib.abs.IViewListener;
import com.akame.commonlib.base.presenter.BasePresenter;
import com.akame.commonlib.utils.InputMethodUtil;
import com.akame.commonlib.utils.LoadingDialog;
import com.akame.commonlib.utils.StatueBarUtil;
import com.akame.commonlib.utils.StatusLayoutUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * @Author: Akame
 * @Date: 2019/2/15
 * @Description: 在baseLibActivity基础上分发基础的业务
 * 主要业务：设置状态栏颜色或者透明 显示加载数据状态 显示隐藏软键盘 显示隐藏加载提示框
 */
public abstract class CommonActivity<T extends BasePresenter> extends BaseLibActivity<T> {
    private boolean isStatueBarLightMode = true;
    private int statueBarColor = CommonLib.getApplication().getResources().getColor(R.color.white);
    private SmartRefreshLayout smartRefreshLayout;
    private IStatusLayoutHelp layoutUtil;
    private LoadingDialog loadingDialog;

    @Override
    protected void setStatueBarColor() {
        //如果当前版本大于等于5.0以上 则进行状态栏修改 否则不进行处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isShowStatueBar()) {
                //让状态栏显示主题颜色
                StatueBarUtil.setStatusBar(this, statueBarColor);
            } else {
                //让状态栏透明
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            StatueBarUtil.setThemeLightMode(this, isStatueBarLightMode);
        }
    }

    /**
     * 设置StatueBar 是否为黑色主题
     */
    protected void setStatueBarDuckMode(boolean isDuckMode) {
        this.isStatueBarLightMode = isDuckMode;
    }

    /**
     * 设置StatueBar背景颜色
     */
    protected void setStatueBarColor(int statueBarColor) {
        this.statueBarColor = statueBarColor;
    }

    /**
     * 是否显示StatueBar
     */
    protected boolean isShowStatueBar() {
        return true;
    }

    /**
     * 拿出刷新view给P层调用
     */
    @Override
    public SmartRefreshLayout getRefreshControl() {
        return smartRefreshLayout;
    }

    /**
     * 设置下拉刷新view
     */
    @Override
    public void setRefreshControl(SmartRefreshLayout refreshControl) {
        this.smartRefreshLayout = refreshControl;
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    protected void showKeyBroad(View view) {
        InputMethodUtil.showKeyBoard(view);
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    protected void hideKeyBrod(View view) {
        InputMethodUtil.hideKeyBoard(view);
    }

    /**
     * 创建页面提示
     *
     * @param currView    创建的view
     * @param clickListen 点击再次请求的监听事件
     */
    public void createLayoutHintView(View currView, final IViewListener.ViewClickListener clickListen) {
        layoutUtil = StatusLayoutUtil.configStatusView(this, currView, clickListen);
    }

    /**
     * 空数据提示页面
     */
    @Override
    public void showEmptyView() {
        if (layoutUtil != null)
            layoutUtil.showEmptyView();
    }

    /**
     * 错误数据提示页面
     */
    @Override
    public void showNetErrorView() {
        if (layoutUtil != null)
            layoutUtil.showNetErrorView();
    }

    /**
     * 默认数据提示页面
     */
    @Override
    public void showDefaultLayout() {
        if (layoutUtil != null)
            layoutUtil.showDefaultView();
    }

    /**
     * 显示加载View
     */
    @Override
    public void showLoadingView() {
        if (layoutUtil != null) {
            layoutUtil.showLoadingView();
        }
    }

    /**
     * 隐藏加载View
     */
    @Override
    public void hideLoadingView() {
        if (layoutUtil != null) {
            layoutUtil.hideLoadingView();
        }
    }

    /**
     * 显示加载提示框
     */
    @Override
    public void showLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.show();
        } else {
            loadingDialog = new LoadingDialog(this);
            showLoadingDialog();
        }
    }

    /**
     * 有些情况下需要预先初始化Dialog
     */
    public void initLodingDialog() {
        loadingDialog = new LoadingDialog(this);
    }

    /**
     * 隐藏加载提示框
     */
    @Override
    public void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放dialog资源
        if (loadingDialog != null) {
            loadingDialog.cancel();
            loadingDialog = null;
        }
        //隐藏键盘 防止在页面销毁的时候键盘还在
        hideKeyBrod(findViewById(android.R.id.content));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1){
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    //还原字体大小
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }
}
