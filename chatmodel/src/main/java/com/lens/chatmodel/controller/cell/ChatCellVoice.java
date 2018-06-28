package com.lens.chatmodel.controller.cell;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.cache.FileManager;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lensim.fingerchat.data.help_class.IProgressListener;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.TDevice;


/**
 * Created by LL130386 on 2018/1/3.
 * 语音
 */

public class ChatCellVoice extends ChatCellBase {

    private ImageView iv_voice;
    private TextView tv_length;

    private final Context mContext;
    private ImageView iv_unread;
    private double minWidth;
    private double maxWidth;
    private double gap;
    private double grade1;
    private double grade2;
    private double grade3;

    protected ChatCellVoice(Context context,
        EChatCellLayout cellLayout, IChatEventListener listener, MessageAdapter adapter,
        int position) {
        super(context, cellLayout, listener, adapter, position);
        mContext = context;
        loadControls();
    }


    private void loadControls() {
        iv_voice = getView().findViewById(R.id.iv_voice);
        tv_length = getView().findViewById(R.id.tv_length);
        iv_unread = getView().findViewById(R.id.iv_unread_voice);


    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            int time = Integer.valueOf(mChatRoomModel.getTimeLength());
            initLength(time);
            updateBubbleWidth(time);
            initPlayAnimation();
            checkPlayStatus();
            setSecretShow(mChatRoomModel.isSecret(), bubbleLayout);
        }

    }

    @Override
    public void onBubbleClick() {
        new VoicePlayClickListener(mChatRoomModel, iv_voice, iv_unread, mAdapter,
            ContextHelper.getContext()).onClick(bubbleLayout);
    }

    private void initLength(int len) {
        if (isErrorShowing() || mChatRoomModel.isSecret()) {
            tv_length.setVisibility(GONE);
            return;
        }
        if (len > 0) {
            tv_length.setVisibility(VISIBLE);
            tv_length.setText(ChatHelper.getTimeLength(len, mChatRoomModel.getMsgType()));
        }
    }

    private void initPlayAnimation() {
        if (VoicePlayClickListener.playMsgId != null
            && VoicePlayClickListener.playMsgId.equals(mChatRoomModel.getMsgId())
            && VoicePlayClickListener.isPlaying) {
            if (mChatRoomModel.isIncoming()) {
                iv_voice.setImageResource(R.drawable.voice_from_icon);
            } else {
                iv_voice.setImageResource(R.drawable.voice_to_icon);
            }
            AnimationDrawable mAnimation = (AnimationDrawable) iv_voice.getDrawable();
            mAnimation.start();
        } else {
            if (mChatRoomModel.isIncoming()) {
                iv_voice.setImageResource(R.drawable.ease_chatfrom_voice_playing);
            } else {
                iv_voice.setImageResource(R.drawable.ease_chatto_voice_playing);
            }
        }
    }


    private void updateBubbleWidth(int len) {
        if (minWidth <= 0) {
            minWidth = TDevice.dpToPixel(56);
        }
        if (maxWidth <= 0) {
            maxWidth = TDevice.dpToPixel(220);
        }
        if (gap <= 0) {
            gap = maxWidth - minWidth;
        }
        if (gap > 0) {
            //适用于1~10s
            if (grade1 <= 0) {
                grade1 = 0.4 * gap / 10;
            }
            //适用于10~30s
            if (grade2 <= 0) {
                grade2 = 0.4 * gap / 20;
            }
            //适用于30以上
            if (grade3 <= 0) {
                grade3 = 0.2 * gap / 30;
            }

            if (len > 0) {
                if (len > 0 && len <= 10) {
                    bubbleLayout.setMinimumWidth((int) (minWidth + grade1 * len));
                } else if (len > 10 && len <= 30) {
                    bubbleLayout
                        .setMinimumWidth((int) (minWidth + grade1 * 10 + grade2 * (len - 10)));
                } else if (len > 30 && len <= 60) {
                    bubbleLayout
                        .setMinimumWidth(
                            (int) (minWidth + grade1 * 10 + grade2 * 20 + grade3 * (len - 30)));
                } else {
                    bubbleLayout.setMinimumWidth((int) maxWidth);
                }
            }
        }

    }

    private void checkPlayStatus() {
        if (mChatRoomModel.isIncoming()) {
            if (mChatRoomModel.getPlayStatus() == EPlayType.NOT_DOWNLOADED) {
                String content = mChatRoomModel.getContent();
                if (ChatHelper.checkHttpUrl(content)) {
                    String voiceUrl = FileCache.getInstance().getVoicePath(content.split("@")[0]);
                    if (!TextUtils.isEmpty(voiceUrl)) {
                        showUnread(true);
                        showVoiceLength(true);
                        showProgress(false);
                    } else {
                        showUnread(true);
                        showVoiceLength(false);
                        showProgress(true);
                        downloadVoice();
                    }
                } else {
                    showUnread(true);
                    showVoiceLength(false);
                    showProgress(true);
                    downloadVoice();
                }
            } else if (mChatRoomModel.getPlayStatus() == EPlayType.NOT_PALYED) {
                showUnread(true);
                showVoiceLength(true);
                showProgress(false);
            } else {
                showUnread(false);

            }
        } else {
            showVoiceLength(true);
        }
    }

    private void showUnread(boolean b) {
        iv_unread.setVisibility(b ? VISIBLE : GONE);
    }

    private void showVoiceLength(boolean b) {
        if (isErrorShowing() || isProgressShowing()) {
            tv_length.setVisibility(GONE);
        } else {
            tv_length.setVisibility(b ? VISIBLE : GONE);
        }
    }

    private void downloadVoice() {
        FileManager.getInstance().downloadFile(mChatRoomModel, new IProgressListener() {
            @Override
            public void onSuccess(byte[] bytes) {
                mChatRoomModel.setPlayStatus(EPlayType.NOT_PALYED);
                ProviderChat.updatePlayStatus(ContextHelper.getContext(), mChatRoomModel.getMsgId(),
                    EPlayType.NOT_PALYED);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void progress(int progress) {
                showProgress(true);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void hasDownload() {

    }

}
