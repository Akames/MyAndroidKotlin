package com.akame.commonlib.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akame.commonlib.R;
import com.akame.commonlib.abs.IStatusLayoutHelp;
import com.akame.commonlib.abs.IViewListener;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * 作者：ZLL
 * 时间：2018/4/18
 * 名称：断网页封装管理
 * 附加注释：
 */
public class StatusLayoutUtil implements IStatusLayoutHelp {
    private View statusLayout;
    private ImageView ivUnusual;
    private TextView tvUnusual;
    private TextView tvReTry;
    private ViewGroup.LayoutParams params;
    private ViewGroup parentLayout;
    private int currentIndex;
    private View contentLayout;
    private int viewType = 0X11; // 页面类型  默认为内容页面 这个值不能随意更改
    //加载提示框
    private AVLoadingIndicatorView loadingView;

    public static StatusLayoutUtil configStatusView(Context context, View currentView, IViewListener.ViewClickListener viewClickListen) {
        return new StatusLayoutUtil(context, currentView, viewClickListen);
    }

    private StatusLayoutUtil(Context context, View currentView, IViewListener.ViewClickListener viewClickListen) {
        createStatueView(context, viewClickListen);
        getCurrentViewParam(currentView);
    }

    private void createStatueView(Context context, final IViewListener.ViewClickListener viewClickListen) {
        statusLayout = LayoutInflater.from(context).inflate(R.layout.layout_not_network, null);
        ivUnusual = statusLayout.findViewById(R.id.iv_unusual);
        tvUnusual = statusLayout.findViewById(R.id.tv_unusual);
        tvReTry = statusLayout.findViewById(R.id.tv_reTry);
        loadingView = statusLayout.findViewById(R.id.av_loading);
        tvReTry.setOnClickListener(v -> {
            if (viewClickListen != null) {
                showLoadingView();
                viewClickListen.onViewClickListener();
            }
            tvReTry.setEnabled(false);
        });
    }

    private void getCurrentViewParam(View currentView) {
        if (currentView == null) {
            return;
        }
        this.contentLayout = currentView;
        //获取目标view父容器
        params = currentView.getLayoutParams();
        if (params != null) {
            parentLayout = (ViewGroup) currentView.getParent();
        } else {
            parentLayout = currentView.getRootView().findViewById(android.R.id.content);
        }
        //通过父容器查找到目标view的位置，并记录下来
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            if (parentLayout.getChildAt(i).getId() == currentView.getId()) {
                this.currentIndex = i;
                break;
            }
        }
    }

    private void showStatueView(View view) {
        if (view != null) {
            //移除掉view的父容器，因为一个view不允许出现两个父容器
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(view);
            }
            //替换目标位置view
            if (this.parentLayout != null) {
                if (this.parentLayout.getChildCount() > currentIndex && this.parentLayout.getChildAt(currentIndex) != null) {
                    this.parentLayout.removeViewAt(this.currentIndex);
                }
                this.parentLayout.addView(view, this.currentIndex, this.params);
            }
        }
    }

    /* 网络异常**/
    @Override
    public void showNetErrorView() {
        int VIEW_TYPE_NETWORK_ERROR = 0X13;
        if (viewType != VIEW_TYPE_NETWORK_ERROR && statusLayout != null) {
            ivUnusual.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            ivUnusual.setImageResource(R.drawable.ic_not_network);
            if (NetWorkUtils.isNetworkAvailable(tvUnusual.getContext())) {
                tvUnusual.setText(R.string.not_network_prompt1);
            } else {
                tvUnusual.setText(R.string.not_network_prompt2);
            }
            tvReTry.setVisibility(View.VISIBLE);
            tvReTry.setEnabled(true);
            showStatueView(statusLayout);
            viewType = VIEW_TYPE_NETWORK_ERROR;
        }
    }

    /* 空数据**/
    @Override
    public void showEmptyView() {
        int VIEW_TYPE_ENTITY_DATA = 0X12;
        if (viewType != VIEW_TYPE_ENTITY_DATA && statusLayout != null) {
            ivUnusual.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            ivUnusual.setImageResource(R.drawable.ic_empty_hint);
            tvUnusual.setText(R.string.empty_data_prompt);
            tvReTry.setVisibility(View.GONE);
            tvReTry.setEnabled(true);
            showStatueView(statusLayout);
            viewType = VIEW_TYPE_ENTITY_DATA;
        }
    }

    /* 初始View**/
    @Override
    public void showDefaultView() {
        int VIEW_TYPE_DEFAULT = 0X11;
        if (viewType != VIEW_TYPE_DEFAULT && contentLayout != null) {
            tvReTry.setEnabled(true);
            showStatueView(contentLayout);
            viewType = VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public void showLoadingView() {
        int VIEW_TYPE_LOADING = 0X10;
        if (viewType != VIEW_TYPE_LOADING && statusLayout != null) {
            ivUnusual.setVisibility(View.GONE);
            tvReTry.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            tvUnusual.setText(R.string.loading_date);
            loadingView.show();
            showStatueView(statusLayout);
            viewType = VIEW_TYPE_LOADING;
        }
    }

    @Override
    public void hideLoadingView() {
        if (statusLayout != null) {
            loadingView.hide();
            loadingView.setVisibility(View.GONE);
        }
    }
}
