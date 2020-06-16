package com.akame.commonlib.base.presenter;

import com.akame.commonlib.base.view.AbsView;

import io.reactivex.disposables.Disposable;

/**
 * @Author: Restring
 * @Date: 2018/4/23
 * @Description:
 */
public interface AbsPresent<T extends AbsView> {
    /**
     * 注入View
     *
     * @param view view
     */
    void attachView(T view);

    /**
     * 回收View
     */
    void detachView();

    /**
     * Add rxBing subscribe manager
     *
     * @param disposable Disposable
     */
    void addRxBindingSubscribe(Disposable disposable);
}
