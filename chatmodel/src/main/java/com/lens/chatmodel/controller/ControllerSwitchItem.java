package com.lens.chatmodel.controller;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.view.SwitchButton;

/**
 * Created by LL130386 on 2018/3/14.
 */

public class ControllerSwitchItem {

    private TextView tv_title;
    private SwitchButton viewSwitch;
    private OnClickListener listener;

    public ControllerSwitchItem(View v) {
        init(v);
    }

    private void init(View v) {
        tv_title = v.findViewById(R.id.tv_title);
        viewSwitch = v.findViewById(R.id.viewSwitch);
        viewSwitch.setOnClickListener(listener);
    }

    public void setTitleText(String txt) {
        tv_title.setText(txt);
    }

    public int getSwitchStatus() {
        return viewSwitch.getCurrentStatus();
    }

    public boolean isOpen() {
        return viewSwitch.isChecked();
    }

    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }
}
