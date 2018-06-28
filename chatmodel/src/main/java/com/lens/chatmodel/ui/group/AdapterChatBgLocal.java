package com.lens.chatmodel.ui.group;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.ChatBgResBean;
import com.lensim.fingerchat.commons.utils.DensityUtil;


/**
 * Created by xhdl0002 on 2018/2/9.
 * 聊天背景res adapter
 */

public class AdapterChatBgLocal extends AbstractRecyclerAdapter<ChatBgResBean> {

    public void setSelectPosition(int backId) {
        this.backId = backId;
    }

    private int backId;

    private RelativeLayout.LayoutParams menuLinerLayoutParames;

    public AdapterChatBgLocal(Context ctx) {
        super(ctx);
        int srcWidth =
            (DensityUtil.getScreenWidth((Activity) ctx) - DensityUtil.dip2px(ctx, 12)) / 3;
        menuLinerLayoutParames = new RelativeLayout.LayoutParams(srcWidth, srcWidth);
        mBeanList.add(new ChatBgResBean(EChatBgId.DEFAULT.id, R.color.white));
        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.FIRST.id, R.drawable.background_1));
        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.SECEND.id, R.drawable.background_2));
        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.THIRD.id, R.drawable.background_3));
        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.FORTH.id, R.drawable.background_4));
        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.FIFTH.id, R.drawable.background_5));
        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.SIXTH.id, R.drawable.background_6));
//        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.SEVENTH.id, R.drawable.background_7));
//        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.EIGHTH.id, R.drawable.background_8));
//        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.NINTH.id, R.drawable.background_9));
//        mBeanList.add(new ChatBgResBean(ChatEnum.EChatBgId.TENTH.id, R.drawable.background_10));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatBgLocalHolder(View.inflate(mContext, R.layout.item_chatbg_local, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ChatBgLocalHolder holder = (ChatBgLocalHolder) viewHolder;
        ChatBgResBean bean = mBeanList.get(position);
        holder.itemChatBg.setLayoutParams(menuLinerLayoutParames);
        holder.itemChatBg.setImageResource(mBeanList.get(position).getResId());
        if (backId == bean.getBackId()) {
            holder.itemChatBgCheck.setVisibility(View.VISIBLE);
        } else {
            holder.itemChatBgCheck.setVisibility(View.GONE);
        }
        holder.itemChatBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(bean);
            }
        });
    }

    class ChatBgLocalHolder extends RecyclerView.ViewHolder {

        ImageView itemChatBg;
        ImageView itemChatBgCheck;

        public ChatBgLocalHolder(View itemView) {
            super(itemView);
            itemChatBg = itemView.findViewById(R.id.item_chatbg_bg);
            itemChatBgCheck = itemView.findViewById(R.id.item_chatbg_check);
        }
    }
}
