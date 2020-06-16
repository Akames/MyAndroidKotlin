package com.akame.commonlib.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.akame.commonlib.R;
import com.akame.commonlib.abs.IPermissionsResultListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseDialog extends Dialog {

    private CompositeDisposable disposable;
    private RxPermissions permissions;
    private Activity activity;

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.BaseDialogStyle);
        initData();
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    public BaseDialog(Context context, int style) {
        super(context, style);
        initData();
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    public abstract void initData();

    protected void addSubscribe(Disposable disposable) {
        if (this.disposable == null) {
            this.disposable = new CompositeDisposable();
        }
        this.disposable.add(disposable);
    }

    private void detachView() {
        if (disposable != null) {
            disposable.clear();
        }
    }

    private RxPermissions getPermissions() {
        if (activity == null) {
            throw new RuntimeException("activity is null");
        }
        if (permissions == null) {
            permissions = new RxPermissions(activity);
        }
        return permissions;
    }


    public void requestPermission(String[] permission, IPermissionsResultListener listener) {
        addSubscribe(getPermissions().request(permission).subscribe(per -> {
            if (per) {
                listener.onSuccess();
            } else {
                new AlertDialog.Builder(activity)
                        .setMessage("为了更好的体验，请前往设置开启"+ PermissionContants.getPermissionName(permission) +"权限")
                        .setPositiveButton("前往设置", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                            activity.startActivity(intent);
                        }).show();
                listener.onFailure();
            }
        }));
    }
}
