package com.lens.chatmodel.controller.cell;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.VideoEventBean;
import com.lens.chatmodel.cache.FileManager;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.view.CustomShapeTransformation;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lensim.fingerchat.data.help_class.IProgressListener;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;


/**
 * Created by LL130386 on 2018/1/3.
 * 视频消息
 */

public class ChatCellVideo extends ChatCellBase {

    private ImageView iv_play;
    private ImageView override;
    private VideoUploadEntity entity;
    private String videoPath;

    protected ChatCellVideo(EChatCellLayout cellLayout, IChatEventListener listener,
        MessageAdapter adapter, int postion) {
        super(cellLayout, listener, adapter, postion);
        loadControls();
    }


    private void loadControls() {
        iv_play = getView().findViewById(R.id.iv_play);
        override = getView().findViewById(R.id.video_override);
    }

    @Override
    public void onBubbleClick() {
        if (entity == null) {
            return;
        }
        if (mChatRoomModel.isIncoming()
            && mChatRoomModel.getPlayStatus() == EPlayType.NOT_DOWNLOADED) {
            if (TextUtils.isEmpty(videoPath)) {
                videoPath = FileCache.getInstance().getVideoPath(entity.getVideoUrl());
            }
            if (!TextUtils.isEmpty(videoPath)) {//转发的消息是同一路径，已经下载过了
                mChatRoomModel.setPlayStatus(EPlayType.NOT_PALYED);
                ProviderChat.updatePlayStatus(ContextHelper.getContext(), mChatRoomModel.getMsgId(),
                    EPlayType.NOT_PALYED);
                if (mAdapter instanceof MessageAdapter) {
                    mAdapter.notifyDataSetChanged();
                }
                AnimationRect rect = AnimationRect.buildFromImageView(override);
                mEventListener
                    .onEvent(ECellEventType.VIDEO_CLICK, mChatRoomModel,
                        new VideoEventBean(rect, videoPath, entity.getImageSize()));
            } else {
                download();
            }
        } else {
            mAdapter.notifyDataSetChanged();
            AnimationRect rect = AnimationRect.buildFromImageView(override);
            if (!ChatHelper.checkHttpUrl(entity.getVideoUrl())) {
                videoPath = entity.getVideoUrl();
            } else {
                videoPath = FileCache.getInstance().getVideoPath(entity.getVideoUrl());
            }
            if (!TextUtils.isEmpty(videoPath)) {
                mEventListener
                    .onEvent(ECellEventType.VIDEO_CLICK, mChatRoomModel,
                        new VideoEventBean(rect, videoPath, entity.getImageSize()));
            }

        }
    }

    private void download() {
        if (entity != null && !TextUtils.isEmpty(entity.getVideoUrl())) {
            FileManager.getInstance().downloadFile(mChatRoomModel, new IProgressListener() {
                @Override
                public void onSuccess(byte[] bytes) {
                    mChatRoomModel.setPlayStatus(EPlayType.NOT_PALYED);
                    ProviderChat
                        .updatePlayStatus(ContextHelper.getContext(), mChatRoomModel.getMsgId(),
                            EPlayType.NOT_PALYED);
                    if (mAdapter instanceof MessageAdapter) {
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void progress(int progress) {
                    updateProgress(progress);
                }

                @Override
                public void onFailed() {
                    System.out.println("视频下载失败");
                }
            });
        }
    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            entity = VideoUploadEntity.fromJson(mChatRoomModel.getContent());
            if (mChatRoomModel.isIncoming()) {
                override.setAlpha(1f);
                if (iv_play.getVisibility() != VISIBLE) {
                    iv_play.setVisibility(VISIBLE);
                }
                checkDownLoadStatus();
                if (entity != null && !TextUtils.isEmpty(entity.getImageUrl())) {
                    loadImage(entity.getImageUrl(), R.drawable.speech_bubble);
                } else {
                    loadImage(entity.getVideoUrl(), R.drawable.speech_bubble);
                }
            } else {
                override.setAlpha(1f);
                if (iv_play.getVisibility() != VISIBLE) {
                    iv_play.setVisibility(VISIBLE);
                }

                if (entity != null && !TextUtils.isEmpty(entity.getImageUrl())) {
                    if (ChatHelper.checkHttpUrl(entity.getVideoUrl())) {
                        checkDownLoadStatus();
                    }
                    loadImage(entity.getImageUrl(), R.drawable.my_sound_burned);
                }
            }

            setSecretShow(mChatRoomModel.isSecret(), null);


        }
    }

    private void checkDownLoadStatus() {
        iv_play.setOnClickListener(v -> {
            if (mChatRoomModel.getPlayStatus() == EPlayType.NOT_DOWNLOADED) {//未下载，先下载
                if (TextUtils.isEmpty(videoPath)) {
                    videoPath = FileCache.getInstance().getVideoPath(entity.getVideoUrl());
                }
                if (!TextUtils.isEmpty(videoPath)) {//转发的消息是同一路径，已经下载过了
                    onBubbleClick();
                } else {
                    download();
                }
            } else {
                onBubbleClick();
            }
        });
    }

    private void loadImage(String url, int res) {
        ImageHelper.loadMessageImage(url, override,
            new CustomShapeTransformation(ContextHelper.getContext(), res, false));
    }

}
