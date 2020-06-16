package com.akame.commonlib;

import android.app.Application;

import com.akame.commonlib.utils.LogUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.smtt.sdk.QbSdk;

/**
 * @Author: Restring
 * @Date: 2018/4/23
 * @Description:
 */
public class CommonLib {
    private static Application application;
    private static RefWatcher refWatcher;

    public static void initApplication(Application application) {
        CommonLib.application = application;
        Logger.addLogAdapter(new AndroidLogAdapter());
        setSmartRefreshLayoutOption();
        initX5();
//        refWatcher = LeakCanary.install(application);
    }

    public static Application getApplication() {
        return application;
    }

//    public static RefWatcher getRefWatcher() {
//        return CommonLib.refWatcher;
//    }

    /**
     * 设置下拉刷新样式
     */
    private static void setSmartRefreshLayoutOption() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setEnableOverScrollDrag(true); //设置启动越界滑动
            layout.setPrimaryColors(getApplication().getResources().getColor(R.color.default_bg), getApplication().getResources().getColor(R.color.text_black));
            layout.setEnableLoadMoreWhenContentNotFull(false);
            return new ClassicsHeader(context).setDrawableSize(14).setTextSizeTitle(12).setTextSizeTime(10);
//            return new MaterialHeader(context).setShowBezierWave(false).setColorSchemeColors(getApplication().getResources().getColor(R.color.theme));
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context).setDrawableSize(14).setTextSizeTitle(12));
    }

    private static void initX5() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
                @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtil.d(" X5 内核加载 " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplication(), cb);
    }

}
