package com.lens.chatmodel.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.SearchMessageBean;
import java.util.List;

/**
 * Created by LL130386 on 2018/4/27.
 */

public class SearchItemAdapter extends BaseAdapter {


    private final LayoutInflater mInflater;
    private String condition;
    private final ISearchEventListener listener;
    private EResultType type;

    SearchItemAdapter(LayoutInflater inflater,ISearchEventListener l) {
        mInflater = inflater;
        listener = l;
    }

    private List<SearchMessageBean> mList;

    public void setData(List<SearchMessageBean> list, String condition,EResultType p) {
        mList = list;
        this.condition = condition;
        type = p;
    }


    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchMessageBean bean = mList.get(position);
        ControllerSearchItem controller;
        if (convertView == null) {
            controller = new ControllerSearchItem(
                mInflater.inflate(R.layout.item_record_cell, null));
            convertView = controller.getView();
            convertView.setTag(controller);
        } else {
            controller = (ControllerSearchItem) convertView.getTag();
        }
        controller.setData(bean, condition,type);
        controller.setListener(listener);
        return convertView;
    }
}
