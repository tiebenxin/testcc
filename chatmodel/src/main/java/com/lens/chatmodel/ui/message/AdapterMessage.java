package com.lens.chatmodel.ui.message;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.controller.ControllerMessageListItem;
import com.lens.chatmodel.interf.IChatItemClickListener;
import com.lens.chatmodel.interf.IController;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2017/11/25.
 */

public class AdapterMessage extends BaseAdapter {

    List<RecentMessage> mList;
    private final Context mContext;
    private IChatItemClickListener listener;
    private String userId;

    public AdapterMessage(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void bindData(List<RecentMessage> l) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.clear();
        mList.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public RecentMessage getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        IController controller;
        RecentMessage model = mList.get(position);
        if (convertView == null) {
            controller = new ControllerMessageListItem(mContext);
            convertView = controller.getView();
            convertView.setTag(controller);
        } else {
            controller = (IController) convertView.getTag();
        }
        if (!TextUtils.isEmpty(userId)) {
            ((ControllerMessageListItem) controller).setUserId(userId);
        }
        if (model != null) {
            controller.setModel(model, position);
        }
        if (listener != null) {
            ((ControllerMessageListItem) controller).setOnClickListener(listener);
        }
        return convertView;
    }

    public void setItemClickListener(IChatItemClickListener l) {
        listener = l;
        notifyDataSetChanged();
    }

    public void setUserId(String id) {
        userId = id;
    }
}
