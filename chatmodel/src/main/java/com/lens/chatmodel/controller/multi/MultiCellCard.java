package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.CardBody;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.helper.GsonHelper;

/**
 * Created by LL130386 on 2018/2/1.
 * 名片转发消息
 */

public class MultiCellCard extends MultiCellBase {

    private ImageView iv_avatar;
    private TextView tv_card_name;
    private TextView tv_userid;
    private CardBody entity;

    protected MultiCellCard(Context context, EMultiCellLayout cellLayout,
        IChatEventListener listener) {
        super(context, cellLayout, listener);
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
        if (mEntity != null) {
            entity = GsonHelper.getObject(mEntity.getBody(), CardBody.class);
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
        if (entity == null) {
            return;
        }
        Intent intentCard = FriendDetailActivity.createNormalIntent(mContext, entity.getFriendId());
        mContext.startActivity(intentCard);
    }
}
