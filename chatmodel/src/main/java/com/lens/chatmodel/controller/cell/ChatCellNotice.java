package com.lens.chatmodel.controller.cell;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EActionType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.MsgTagHandler;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IActionTagClickListener;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.ui.group.GroupInviteManageActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import java.util.ArrayList;


/**
 * Created by LL130386 on 2018/2/1.
 * action 消息
 */

public class ChatCellNotice extends ChatCellBase implements IActionTagClickListener {

    private final long VIBRATE_DURATION = 200L;


    protected ChatCellNotice(EChatCellLayout cellLayout, IChatEventListener listener,
        MessageAdapter adapter, int postion) {
        super(cellLayout, listener, adapter, postion);
    }


    @RequiresApi(api = VERSION_CODES.N)
    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null && tv_notify != null) {
            initText(tv_notify, mChatRoomModel.getContent(), mChatRoomModel.getMsgId());
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = VERSION_CODES.N)
    private void initText(TextView tv_msg, String content, String msgId) {
        if (isCancel()) {
            if (mChatRoomModel.isIncoming()) {
                tv_msg.setText(String.format(ContextHelper.getString(R.string.cancel_message),
                    mChatRoomModel.getNick()));
            } else {
                tv_msg.setText(ContextHelper.getString(R.string.cancel_message_you));
            }
        } else {
            if (!TextUtils.isEmpty(content)) {
                BodyEntity entity = new BodyEntity(content);
                if (entity != null && !TextUtils.isEmpty(entity.getBody())) {
                    if (entity.getBody()
                        .equalsIgnoreCase(ContextHelper.getString(R.string.shake_content))) {
                        if (mChatRoomModel.isIncoming()) {
                            tv_msg
                                .setText("\'" + mChatRoomModel.getNick() + "\'" + entity.getBody());
                            if (mChatRoomModel.getPlayStatus() != EPlayType.PALYED) {
                                shake();
                            }
                        } else {
                            tv_msg.setText("你" + entity.getBody());
                        }
                    }
                }
            }
        }
    }


    private boolean isCancel() {
        if (mChatRoomModel.getCancel() == 1) {
            return true;
        } else {
            return false;
        }
    }

    private void shake() {
        Vibrator vibrator = (Vibrator) ContextHelper.getContext()
            .getSystemService(ContextHelper.getContext().VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATE_DURATION);
        mChatRoomModel.setPlayStatus(EPlayType.PALYED);
        ProviderChat.updatePlayStatus(ContextHelper.getContext(), mChatRoomModel.getMsgId(),
            EPlayType.PALYED);

    }


    @Override
    public void clickUser(String userId) {
        Intent intent = FriendDetailActivity
            .createNormalIntent(ContextHelper.getContext(), userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextHelper.getContext().startActivity(intent);
    }

    @Override
    public void clickValidation(String msgId, String userId) {
        Intent intent = new Intent(ContextHelper.getContext(),
            GroupInviteManageActivity.class);
        intent.putExtra("msgId", msgId);
        intent.putExtra("inviteId", userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextHelper.getContext().startActivity(intent);
    }

    @Override
    public void clickCancelInvite(ArrayList<String> userIds) {
        MucManager.getInstance()
            .cancelInvite(userIds, UserInfoRepository.getUserName(), mChatRoomModel.getTo());

    }

    @Override
    public void sendVerify(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            FingerIM.I.inviteFriend(userId);
        }
    }
}
