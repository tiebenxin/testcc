package com.lensim.fingerchat.fingerchat.ui.me;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.fingerchat.R;

/**
 * Created by LL130386 on 2018/2/10.
 */

public class ControllerInfoAvatar {

    private TextView tv_title;
    private ImageView iv_img;
    private View viewRoot;
    private IViewAvatarClickListener listenter;

    public ControllerInfoAvatar(View v) {
        init(v);
    }

    private void init(View v) {
        viewRoot = v;
        tv_title = v.findViewById(R.id.tv_title);
        iv_img = v.findViewById(R.id.iv_avatar);

        viewRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenter != null) {
                    listenter.clickItem();
                }

            }
        });

        iv_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenter != null) {
                    listenter.clickAvatar();
                }
            }
        });
    }


    public void setTitleAndContent(String title, String url) {
        tv_title.setText(title);
        ImageHelper.loadAvatarPrivate(url, iv_img);
    }

    public void setAvatar(String url) {
        ImageHelper.loadAvatarPrivate(url, iv_img);
    }


    public void setOnClickListener(IViewAvatarClickListener l) {
        listenter = l;
    }

}
