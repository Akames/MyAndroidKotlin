package com.akame.commonlib.base;

import android.text.TextUtils;

import com.akame.commonlib.CommonLib;
import com.akame.commonlib.base.view.AbsView;
import com.akame.commonlib.utils.NetWorkUtils;

import io.reactivex.observers.ResourceObserver;

/**
 * @Author: Restring
 * @Date: 2018/4/24
 * @Description:
 */
public abstract class BaseLibObserver<T> extends ResourceObserver<T> {
    private AbsView mView; //显示View的引用
    private String mErrorMsg; // 自定义错误提示
    private boolean isShowErrorView = true; //请求错误的时候 是否显示错误的页面 默认显示
    private boolean isShowErrorToast = true;  //请求错的的时候 是否显示错的的提示  默认显示

    protected abstract void onRequestSuccess(T data);

    protected BaseLibObserver(AbsView view) {
        this.mView = view;
    }

    protected BaseLibObserver(AbsView view, String errorMsg) {
        this.mView = view;
        this.mErrorMsg = errorMsg;
    }

    protected BaseLibObserver(AbsView view, boolean isShowErrorView) {
        this.mView = view;
        this.isShowErrorView = isShowErrorView;
    }

    protected BaseLibObserver(AbsView view, boolean isShowErrorView, boolean isShowErrorToast) {
        this.mView = view;
        this.isShowErrorToast = isShowErrorToast;
        this.isShowErrorView = isShowErrorView;
    }

    protected BaseLibObserver(AbsView view, String errorMsg, boolean isShowErrorView) {
        this.mView = view;
        this.mErrorMsg = errorMsg;
        this.isShowErrorView = isShowErrorView;
    }

    public BaseLibObserver setErrorMsg(String mErrorMsg) {
        this.mErrorMsg = mErrorMsg;
        return this;
    }

    public BaseLibObserver setShowErrorView(boolean isShowErrorView) {
        this.isShowErrorView = isShowErrorView;
        return this;
    }

    public BaseLibObserver setShowErrorToast(boolean isShowErrorToast) {
        this.isShowErrorToast = isShowErrorToast;
        return this;
    }

    @Override
    public void onComplete() {
        if (mView != null) {
            mView.hideLoadingView();
            mView.hideLoadingDialog();
        }
    }

    @Override
    public void onNext(T t) {
        onRequestSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        if (mView != null) {
            if (isShowErrorView) {
                mView.showNetErrorView();
            }
            if (isShowErrorToast) {
                if (!NetWorkUtils.isNetworkAvailable(CommonLib.getApplication())) {
                    mView.showMsg("网络异常");
                } else {
                    if (TextUtils.isEmpty(mErrorMsg)) {
                        mView.showMsg(e.getMessage());
                    } else {
                        mView.showMsg(mErrorMsg);
                    }
                }
            }
        }
        onComplete();
    }

}