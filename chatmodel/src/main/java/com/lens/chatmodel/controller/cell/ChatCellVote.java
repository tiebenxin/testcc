package com.lens.chatmodel.controller.cell;

import android.text.Spannable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.body.VoteBody;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.helper.GsonHelper;


/**
 * Created by LL130386 on 2018/1/3.
 * 投票
 */

public class ChatCellVote extends ChatCellBase {

    private TextView tvOption1;
    private TextView tvOption2;
    private TextView tvTitle;
    private VoteBody entity;
    private Spannable spannable;

    protected ChatCellVote(EChatCellLayout cellLayout, IChatEventListener listener,
        MessageAdapter adapter, int position) {
        super(cellLayout, listener, adapter, position);
        loadControls();
    }


    private void loadControls() {
        tvOption1 = getView().findViewById(R.id.tv_option1);
        tvOption2 = getView().findViewById(R.id.tv_option2);
        tvTitle = getView().findViewById(R.id.tv_title);


    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            String content = mChatRoomModel.getContent();
            entity = GsonHelper.getObject(content, VoteBody.class);
            if (entity == null) {
                return;
            }
            tvTitle.setText(entity.getTitle());
            tvOption1.setText(entity.getOption1());
            tvOption2.setText(entity.getOption2());
            if (entity.getStatus() == 1) {//参与投票消息
                tv_notify.setVisibility(View.VISIBLE);
                rl_root.setVisibility(View.GONE);
                if (mChatRoomModel.getNick() != null) {
                    spannable = SpannableUtil
                        .getVoteHint(mChatRoomModel.getNick(), entity.getTitle());
                    tv_notify.setText(spannable);
                }
                tv_notify.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEventListener != null) {
                            mEventListener
                                .onEvent(ECellEventType.VOTE_CLICK, mChatRoomModel, entity);
                        }
                    }
                });
            } else {
                tv_notify.setVisibility(View.GONE);
                rl_root.setVisibility(View.VISIBLE);
            }

            setSecretShow(mChatRoomModel.isSecret(), bubbleLayout);

        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mEventListener != null) {
            mEventListener
                .onEvent(ECellEventType.VOTE_CLICK, mChatRoomModel, entity);
        }
    }
}
