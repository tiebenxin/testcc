package com.lensim.fingerchat.fingerchat.ui.me;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2018/2/10.
 */

public class ControllerInfoText {

    private TextView tv_title;
    private TextView tv_content;
    private View viewRoot;
    private OnControllerClickListenter listenter;

    public ControllerInfoText(View v) {
        init(v);
    }

    private void init(View v) {
        viewRoot = v;
        tv_title = v.findViewById(R.id.tv_title);
        tv_content = v.findViewById(R.id.tv_content);

        viewRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenter != null) {
                    listenter.onClick();
                }

            }
        });
    }


    public void setTitleAndContent(String title, String content) {
        tv_title.setText(title);
        tv_content.setText(content);
    }

    public String getContent() {
        return (String) tv_content.getText();
    }


    public void setContent(String var) {
        tv_content.setText(var);
    }

    public void setOnClickListener(OnControllerClickListenter l) {
        listenter = l;
    }

}
