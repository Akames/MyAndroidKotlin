package com.akame.commonlib.abs;

import android.view.View;

/**
 * @Author: Administrator
 * @Date: 2018/10/17
 * @Description: view的点击事件
 */
public interface IViewListener {
    //列表中的点击事件
    interface ItemClickListener {
        void onItemClickListener(View view, int pos);
    }

    //单个view的点击事件
    interface ViewClickListener {
        void onViewClickListener();
    }
}
