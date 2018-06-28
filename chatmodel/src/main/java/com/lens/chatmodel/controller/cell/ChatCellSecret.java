package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.interf.IChatEventListener;


/**
 * Created by LL130386 on 2018/1/3.
 * 密聊
 */

public class ChatCellSecret extends ChatCellBase {


    private TextView tv_msg;
    private BodyEntity entity;
    private ImageView iv_lock;

    protected ChatCellSecret(Context context,
        EChatCellLayout cellLayout, IChatEventListener listener, MessageAdapter adapter,
        int position) {
        super(context, cellLayout, listener, adapter, position);
        loadControls();
    }


    private void loadControls() {
        tv_msg = getView().findViewById(R.id.tv_msg);
        iv_lock = getView().findViewById(R.id.iv_lock);
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (!TextUtils.isEmpty(entity.getBody())) {
            mEventListener.onEvent(ECellEventType.SECRET, mChatRoomModel, null);
        }
    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            entity = new BodyEntity(mChatRoomModel.getBody());
            if (!TextUtils.isEmpty(entity.getBody())) {
                tv_msg.setText(ChatHelper.getSecretText(mChatRoomModel.getMsgType()));
                iv_lock.setImageResource(R.drawable.unread_icon);
            } else {
                tv_msg.setText("已阅读");
                iv_lock.setImageResource(R.drawable.alreadyread);

            }
        }
    }


}
