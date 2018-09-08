package com.lens.chatmodel.controller;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2018/3/23.
 */

public class ControllerNetError {

    private View rootView;
    private OnControllerClickListenter listenter;
    private TextView tv_hint;

    public ControllerNetError(View v) {
        initView(v);
    }

    private void initView(View v) {
        rootView = v;
        tv_hint = v.findViewById(R.id.tv_hint);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenter != null) {
                    listenter.onClick();
                }
//                updateHint("正在重连...");
            }
        });
    }

    public void setControllerListener(OnControllerClickListenter l) {
        listenter = l;
    }

    public void setVisiable(boolean value) {
        if (rootView == null) {
            return;
        }
        rootView.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    public void updateHint(String hint) {
        if (tv_hint == null) {
            return;
        }
        tv_hint.setText(hint);
    }

}
