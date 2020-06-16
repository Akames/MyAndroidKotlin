package com.akame.commonlib.base;

/**
 * @Author: Restring
 * @Date: 2018/4/23
 * @Description:
 */

import android.text.TextUtils;

import com.akame.commonlib.CommonLib;
import com.akame.commonlib.R;
import com.akame.commonlib.base.view.AbsView;
import com.akame.commonlib.network.ServerException;

import io.reactivex.subscribers.ResourceSubscriber;
import retrofit2.HttpException;

public abstract class BaseSubscribe<T> extends ResourceSubscriber<T> {

    private AbsView mView;
    private String mErrorMsg;
    private boolean isShowError = true;

    protected BaseSubscribe(AbsView view) {
        this.mView = view;
    }

    protected BaseSubscribe(AbsView view, String errorMsg) {
        this.mView = view;
        this.mErrorMsg = errorMsg;
    }

    protected BaseSubscribe(AbsView view, boolean isShowError) {
        this.mView = view;
        this.isShowError = isShowError;
    }

    protected BaseSubscribe(AbsView view, String errorMsg, boolean isShowError) {
        this.mView = view;
        this.mErrorMsg = errorMsg;
        this.isShowError = isShowError;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        if (mView == null) {
            return;
        }
        if (mErrorMsg != null && !TextUtils.isEmpty(mErrorMsg)) {
            mView.showMsg(mErrorMsg);
        } else if (e instanceof ServerException) {
            mView.showMsg(e.toString());
        } else if (e instanceof HttpException) {
            mView.showMsg(CommonLib.getApplication().getString(R.string.http_error));
        } else {
            mView.showMsg(CommonLib.getApplication().getString(R.string.unKnown_error));
//            LogHelper.d(e.toString());
        }
        if (isShowError) {
            mView.showNetErrorView();
        }
    }
}