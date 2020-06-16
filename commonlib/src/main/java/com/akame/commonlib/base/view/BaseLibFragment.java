package com.akame.commonlib.base.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akame.commonlib.abs.IPermissionsResultListener;
import com.akame.commonlib.base.presenter.AbsPresent;
import com.akame.commonlib.eventbus.Event;
import com.akame.commonlib.eventbus.EventBusUtil;
import com.akame.commonlib.utils.PermissionContants;
import com.akame.commonlib.utils.TUtil;
import com.akame.commonlib.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @Author: Restring
 * @Date: 2018/4/23
 * @Description:
 */
public abstract class BaseLibFragment<T extends AbsPresent> extends Fragment implements AbsView {
    protected T mPresenter;
    private RxPermissions permissions;
    private Unbinder unBinder;
    private CompositeDisposable mCompositeDisposable;
    private boolean isRegisterEventBus = false;
    protected Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unBinder = ButterKnife.bind(this, view);
        mCompositeDisposable = new CompositeDisposable();
        mActivity = getActivity();
        mPresenter = TUtil.getT(this, 0);
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initView();
        initListen();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
        if (unBinder != null) {
            unBinder.unbind();
        }
        if (isRegisterEventBus) {
            EventBusUtil.unregister(this);
        }
        super.onDestroyView();
    }

    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void showMsg(String errorMsg) {
        ToastUtil.showToast(errorMsg);
    }

    @Override
    public void requestPermission(String[] permission, IPermissionsResultListener listener) {
        addSubscribe(getPermissions().request(permission).subscribe(per -> {
            if (per) {
                listener.onSuccess();
            } else {
                new AlertDialog.Builder(mActivity)
                        .setMessage("为了更好的体验，请前往设置开启" + PermissionContants.getPermissionName(permission) + "权限")
                        .setPositiveButton("前往设置", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                            startActivity(intent);
                        }).show();
                listener.onFailure();
            }
        }));
    }

    private RxPermissions getPermissions() {
        if (permissions == null) {
            permissions = new RxPermissions(mActivity);
        }
        return permissions;
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected void registerEventBus() {
        this.isRegisterEventBus = true;
        EventBusUtil.register(this);
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initListen();

    protected abstract void initData();

    protected void onEventBusCome(Event event) {
    }

    protected void onStickyEventBusCome(Event event) {
    }
}
