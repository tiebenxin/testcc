package com.lens.chatmodel.ui.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.AllResult;

/**
 * Created by LL130386 on 2018/4/27.
 */

public class ControllerBaseSearch extends RecyclerView.ViewHolder {

    private TextView tv_key;
    private ControllerLinearList controllerLinearList;
    private LinearLayout ll_more_result;
    private final LayoutInflater mInflater;
    private final ISearchEventListener listener;
    private AllResult selectBean;

    public ControllerBaseSearch(View itemView, LayoutInflater inflater, ISearchEventListener l) {
        super(itemView);
        mInflater = inflater;
        listener = l;
        initView(itemView);
    }

    private void initView(View v) {
        tv_key = v.findViewById(R.id.tv_key);
        controllerLinearList = new ControllerLinearList(v.findViewById(R.id.ll_parent));
        ll_more_result = v.findViewById(R.id.ll_more_result);
        ll_more_result.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectBean != null) {
                    listener.clickMore(EResultType.fromInt(selectBean.getKey()), selectBean);
                }
            }
        });
    }

    public void setData(AllResult bean, String key) {
        if (bean == null) {
            return;
        }
        selectBean = bean;
        tv_key.setText(getTitle(EResultType.fromInt(bean.getKey())));
        SearchItemAdapter itemAdapter = new SearchItemAdapter(mInflater, listener);
        itemAdapter.setData(bean.getResults(), key, EResultType.fromInt(bean.getKey()));
        controllerLinearList.setAdapter(itemAdapter);
        if (bean.getResults() != null) {
            if (bean.getResults().size() < 3) {
                ll_more_result.setVisibility(View.GONE);
            } else {
                ll_more_result.setVisibility(View.VISIBLE);

            }

        }
    }

    private String getTitle(EResultType type) {
        switch (type) {
            case CONTACT:
                return "联系人";
            case MUC:
                return "群聊";
            case RECORD:
                return "聊天记录";
            default:
                return "";
        }
    }
}
