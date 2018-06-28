package com.lensim.fingerchat.fingerchat.ui.me;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2018/2/10.
 */

public class ControllerInfoImage {

    private TextView tv_title;
    private ImageView iv_img;
    private View viewRoot;
    private OnControllerClickListenter listenter;

    public ControllerInfoImage(View v) {
        init(v);
    }

    private void init(View v) {
        viewRoot = v;
        tv_title = v.findViewById(R.id.tv_title);
        iv_img = v.findViewById(R.id.iv_img);

        viewRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenter != null) {
                    listenter.onClick();
                }

            }
        });
    }


    public void setTitleAndContent(String title, int drawableId) {
        tv_title.setText(title);
        iv_img.setImageResource(drawableId);
    }


    public void setOnClickListener(OnControllerClickListenter l) {
        listenter = l;
    }

}
