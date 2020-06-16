package com.akame.commonlib.base;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Author: Administrator
 * @Date: 2018/9/27
 * @Description:
 */
public abstract class BaseObserver2<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
