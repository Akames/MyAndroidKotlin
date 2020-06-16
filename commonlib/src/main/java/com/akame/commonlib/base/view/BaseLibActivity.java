package com.akame.commonlib.base.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.akame.commonlib.abs.IPermissionsResultListener;
import com.akame.commonlib.base.presenter.AbsPresent;
import com.akame.commonlib.eventbus.Event;
import com.akame.commonlib.eventbus.EventBusUtil;
import com.akame.commonlib.utils.ActivityCollector;
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
 * @Description: 基础Activity  主要业务 分级加载初始化View 权限请求 网络回收 注册eventBus
 */
public abstract class BaseLibActivity<T extends AbsPresent> extends AppCompatActivity implements AbsView {
    protected T mPresenter;
    private RxPermissions permissions;
    private Unbinder unBinder;
    private Activity mActivity;
    private boolean isRegisterEventBus = false;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        onViewCreated();
        setStatueBarColor();
        ActivityCollector.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        if (isRegisterEventBus) {
            EventBusUtil.unregister(this);
        }
        unBinder.unbind();
        detachView();
        ActivityCollector.getInstance().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void showMsg(String msg) {
        ToastUtil.showToast(msg);
    }

    @Override
    public void requestPermission(String[] permission, IPermissionsResultListener listener) {
        addSubscribe(getPermissions().request(permission).subscribe(per -> {
            if (per) {
                listener.onSuccess();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("为了更好的体验，请前往设置开启" + PermissionContants.getPermissionName(permission) + "权限")
                        .setPositiveButton("前往设置", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                        }).show();
                listener.onFailure();
            }
        }));
    }

    protected RxPermissions getPermissions() {
        if (permissions == null) {
            permissions = new RxPermissions(this);
        }
        return permissions;
    }

    protected void onViewCreated() {
        mPresenter = TUtil.getT(this, 0);
        unBinder = ButterKnife.bind(this);
        mActivity = this;
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initView();
        initListen();
        initData();
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

    protected void addSubscribe(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    private void detachView() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initListen();

    protected abstract void initData();

    protected abstract void setStatueBarColor();

    //接受EventBus发送的数据 业务activity需要重写这个方法 并加上 @Subject()注解 这里只是为了方便和规范把方法定义出来
    public void onEventBusCome(Event event) {
    }

    //接受EventBus粘性事件
    public void onStickyEventBusCome(Event event) {
    }

}
