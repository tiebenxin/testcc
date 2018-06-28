package com.lensim.fingerchat.fingerchat.ui.me;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.interf.IMeTitleListener;

/**
 * Created by LL130386 on 2017/11/24.
 * Me tab页标题
 */

public class ControllerMeTitle {

    private ImageView iv_head;
    private TextView tv_name;
    private IMeTitleListener listener;
    private View viewRoot;
    private LinearLayout ll_identify;

    ControllerMeTitle(View v) {
        init(v);
    }

    private void init(View v) {
        viewRoot = v;
        iv_head = v.findViewById(R.id.iv_head);
        tv_name = v.findViewById(R.id.tv_name);
        ImageView iv_code = v.findViewById(R.id.iv_code);
        ll_identify = v.findViewById(R.id.ll_identify);

        iv_head.setOnClickListener(view -> {
            if (listener != null) {
                listener.clickAvatar();
            }
        });

        iv_code.setOnClickListener(view -> {
            if (listener != null) {
                listener.clickCode();
            }
        });

    }

    void initUserInfo(UserInfo info) {
        ImageHelper.loadUserImage(info.getImage(), R.drawable.ic_me_head_default, iv_head);
        tv_name.setText(StringUtils.getUserNick(info.getUsernick(), info.getUserid()));
        ll_identify
            .setVisibility(
                info.getIsvalid() == ESureType.YES.ordinal() ? View.VISIBLE : View.INVISIBLE);
    }


    public void setVisible(boolean b) {
        if (b) {
            viewRoot.setVisibility(View.VISIBLE);
        } else {
            viewRoot.setVisibility(View.GONE);

        }
    }

    public void setOnClickListener(IMeTitleListener l) {
        listener = l;
    }

}
