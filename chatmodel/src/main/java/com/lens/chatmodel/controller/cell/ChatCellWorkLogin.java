package com.lens.chatmodel.controller.cell;

import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.work_center.SignInJsonRet;
import com.lensim.fingerchat.data.work_center.sign.SignInJsonAttachInfo;


/**
 * Created by LL130386 on 2018/1/3.
 * 外出考勤打卡
 */

public class ChatCellWorkLogin extends ChatCellBase {

    private TextView tv_sign_time, tv_adress;
    private SignInJsonRet mData;
    private SignInJsonAttachInfo signInEntity;

    protected ChatCellWorkLogin(EChatCellLayout cellLayout, IChatEventListener listener,
        MessageAdapter adapter, int position) {
        super(cellLayout, listener, adapter, position);
        loadControls();
    }


    private void loadControls() {
        tv_sign_time = getView().findViewById(R.id.tv_clock_in_time);
        tv_adress = getView().findViewById(R.id.tv_address);
    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            signInEntity = GsonHelper
                .getObject(mChatRoomModel.getContent(), SignInJsonAttachInfo.class);
            if (signInEntity != null) {
                String address = signInEntity.getPcardlocationInfo();
                String[] names;
                try {
                    names = address.split(StringUtils.SPLIT_COMER);
                    if (names.length > 1) {
                        address = names[0] + "\n" + names[1];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tv_sign_time.setText(String.format(ContextHelper.getString(R.string.sign_in_outer),
                    TimeUtils.getDateHourString(
                        TimeUtils.getTimeStampNoSeconds(signInEntity.getPcardtime()) + "")));
                tv_adress.setText(address);
            }

        }
    }

    @Override
    public void onBubbleClick() {
        create();
        if (mEventListener != null && mData != null) {
            mEventListener.onEvent(ECellEventType.CLOCK_CLICK, mChatRoomModel, mData);
        }
    }

    private void create() {
        if (signInEntity == null) {
            return;
        }
        mData = new SignInJsonRet();
        mData.setSignInTime(signInEntity.getPcardtime());
        mData.setForReport(signInEntity.getPcardforreport());
        mData.setForReportNick(signInEntity.getPcardforreport());
        mData.setRemark(signInEntity.getPcardremark());
        mData.setLocationData(signInEntity.getPcardlocationInfo());
        mData.setTPSignIn(signInEntity.getPcardimages());

    }
}
