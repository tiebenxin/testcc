package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2017/11/30.
 */

public class ControllerContactHeadCell {

    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_unread;
    private OnControllerClickListenter listenter;

    public ControllerContactHeadCell(View v) {
        init(v);
    }

    private void init(View v) {
        iv_avatar = v.findViewById(R.id.iv_avatar);
        tv_name = v.findViewById(R.id.tv_name);
        tv_unread = v.findViewById(R.id.tv_unread);
        tv_unread.setVisibility(View.GONE);

        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenter != null) {
                    listenter.onClick();
                }
            }
        });

        int factor = SPHelper.getInt("font_size", 1) * 2;
        if (factor <= 2) {
            factor = 2;
        }
        tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 12);

    }

    public void setAvatar(int drawable) {
        iv_avatar.setImageDrawable(ContextHelper.getDrawable(drawable));
    }

    public void setAvatar(String url) {
        ImageHelper.loadAvatarPrivate(url, iv_avatar);
    }

    public void setName(String s) {
        tv_name.setText(s);
    }

    public void setUnread(int count) {
        if (tv_unread != null) {
            if (count > 0) {
                tv_unread.setVisibility(View.VISIBLE);
                tv_unread.setText(count + "");
            } else {
                tv_unread.setVisibility(View.GONE);
            }
        }
    }

    public void setOnClickListener(OnControllerClickListenter l) {
        listenter = l;
    }

}
