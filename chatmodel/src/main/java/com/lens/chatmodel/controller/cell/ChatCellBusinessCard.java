package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.body.CardBody;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lensim.fingerchat.commons.helper.GsonHelper;


/**
 * Created by LL130386 on 2018/1/3.
 * 个人名片
 */

public class ChatCellBusinessCard extends ChatCellBase {


    private ImageView iv_avatar;

    private final Context mContext;
    private TextView tv_card_name;
    private TextView tv_userid;

    protected ChatCellBusinessCard(Context context,
        EChatCellLayout cellLayout, IChatEventListener listener, MessageAdapter adapter,
        int position) {
        super(context, cellLayout, listener, adapter, position);
        mContext = context;

        loadControls();
    }


    private void loadControls() {
        iv_avatar = getView().findViewById(R.id.iv_card_avatar);
        tv_card_name = getView().findViewById(R.id.tv_card_usernick);
        tv_userid = getView().findViewById(R.id.tv_card_username);
    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            CardBody entity = GsonHelper.getObject(mChatRoomModel.getBody(), CardBody.class);
            if (entity != null) {
                tv_card_name
                    .setText(TextUtils.isEmpty(entity.getFriendName()) ? entity.getFriendId()
                        : entity.getFriendName());
                tv_userid.setText(entity.getFriendId());
                ImageHelper.loadAvatarPrivate(entity.getFriendHeader(), iv_avatar);
            }
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mEventListener != null) {
            mEventListener.onEvent(ECellEventType.CARD_CLICK, mChatRoomModel, null);
        }
    }


}
