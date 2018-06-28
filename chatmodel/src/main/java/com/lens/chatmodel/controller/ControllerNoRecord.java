package com.lens.chatmodel.controller;


import android.view.View;

/**
 * Created by LL130386 on 2018/3/9.
 * 暂无记录
 */

public class ControllerNoRecord {

    private View rootView;

    public ControllerNoRecord(View v) {
        rootView = v;
    }

    public void setVisible(boolean visible) {
        rootView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
