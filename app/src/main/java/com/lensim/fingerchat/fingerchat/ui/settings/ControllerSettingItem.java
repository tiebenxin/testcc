package com.lensim.fingerchat.fingerchat.ui.settings;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2017/11/23.
 */

public class ControllerSettingItem {

    private ImageView iv_icon;
    private TextView tv_content;
    private View viewRoot;
    private OnControllerClickListenter listenter;

    public ControllerSettingItem(View v) {
        init(v);
    }

    private void init(View v) {
        viewRoot = v;
        iv_icon = v.findViewById(R.id.iv_icon);
        tv_content = v.findViewById(R.id.tv_content);

        viewRoot.setOnClickListener(new OnClickListener() {
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
        tv_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 12);
    }


    public void setIconAndText(Drawable drawable, String content) {
        iv_icon.setImageDrawable(drawable);
        tv_content.setText(content);
    }

    public void setOnClickListener(OnControllerClickListenter l) {
        listenter = l;
    }

    public void setVisible(boolean flag) {
        viewRoot.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

}
