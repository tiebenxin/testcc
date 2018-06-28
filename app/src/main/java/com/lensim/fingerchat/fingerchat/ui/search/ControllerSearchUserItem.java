package com.lensim.fingerchat.fingerchat.ui.search;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter.OnItemClickListener;
import com.lens.chatmodel.bean.SearchTableBean;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.components.adapter.AbstractViewHolder;
import com.lensim.fingerchat.fingerchat.R;

/**
 * Created by LL130386 on 2017/11/30.
 */

public class ControllerSearchUserItem extends AbstractViewHolder<SearchTableBean> {


    private ImageView iv_avatar;
    private TextView tv_name;
    private LinearLayout ll_identify;
    private TextView tv_company;
    private TextView tv_department;
    private OnItemClickListener listenter;
    private SearchTableBean model;

    public ControllerSearchUserItem(View v, OnItemClickListener l) {
        super(v);
        init(v);
        updateWidth(v);
        listenter = l;
    }

    @Override
    public void bindData(SearchTableBean item) {
        if (item != null) {
            model = item;
            ImageHelper.loadAvatarPrivate(item.getAvatarUrl(), iv_avatar);
            tv_name
                .setText(
                    TextUtils.isEmpty(item.getUserNick()) ? item.getUserId() : item.getUserNick());
            ll_identify
                .setVisibility(item.isValid() ? View.VISIBLE : View.GONE);
            tv_company.setText(item.getEmpName());
            tv_department.setText(item.getDptName());
        }

    }

    private void init(View v) {
        iv_avatar = v.findViewById(R.id.iv_avatar);
        tv_name = v.findViewById(R.id.tv_name);
        ll_identify = v.findViewById(R.id.ll_identify);
        tv_company = v.findViewById(R.id.tv_company);
        tv_department = v.findViewById(R.id.tv_department);

        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenter != null && model != null) {
                    listenter.onItemClick(model);
                }
            }
        });
    }

    private void updateWidth(View v) {
        v.setMinimumWidth((int) TDevice.getScreenWidth());

    }

}
