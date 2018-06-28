package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.base.ChatEnvironment;
import com.lens.chatmodel.bean.Emojicon;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;


/**
 * Created by LL130386 on 2018/1/3.
 * 动态表情
 */

public class ChatCellEmoticon extends ChatCellBase {

    private final int DEFAULT_W = DensityUtil.dip2px(ContextHelper.getContext(), 200);
    private final int DEFAULT_H = DensityUtil.dip2px(ContextHelper.getContext(), 120);
    private ImageView iv_content;

    private final Context mContext;

    protected ChatCellEmoticon(Context context,
        EChatCellLayout cellLayout, IChatEventListener listener, MessageAdapter adapter,
        int position) {
        super(context, cellLayout, listener, adapter, position);
        mContext = context;
        loadControls();
    }


    private void loadControls() {
        iv_content = getView().findViewById(R.id.iv_content);
    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            Emojicon emojicon = null;
            if (ChatEnvironment.getInstance().getEmojiconInfoProvider() != null) {
                emojicon = ChatEnvironment.getInstance().getEmojiconInfoProvider()
                    .getEmojiconInfo(mChatRoomModel.getContent());
                if (emojicon != null) {
                    if (emojicon.getBigIcon() != 0) {
                        ImageHelper
                            .loadImageOverrideSize(emojicon.getBigIcon(), iv_content, DEFAULT_W,
                                DEFAULT_H);
                    } else if (emojicon.getBigIconPath() != null) {
                        loadImage(emojicon.getBigIconPath(), "");
                    } else {
                        loadImage("", "");
                    }
                } else {
                    ImageUploadEntity entity = ImageUploadEntity
                        .fromJson(mChatRoomModel.getContent());
                    if (entity != null) {
                        loadImage(entity.getOriginalUrl(), entity.getOriginalSize());
                    } else {
                        iv_content.setImageResource(R.drawable.ease_default_image);
                    }
                }
            }
            setSecretShow(mChatRoomModel.isSecret(), null);

        }
    }

    private void loadImage(String url, String size) {
        if (!TextUtils.isEmpty(size)) {
            String[] arr = size.split("x");
            if (arr == null || arr.length != 2) {
                if (ContextHelper.isGif(url)) {
                    ImageHelper.loadImageGif(url, iv_content, DEFAULT_W, DEFAULT_H);
                } else {
                    ImageHelper.loadImageOverrideSize(url, iv_content, DEFAULT_W, DEFAULT_H);
                }
                return;
            }
            int w = Integer.parseInt(arr[0]);
            int h = Integer.parseInt(arr[1]);
            int width = DEFAULT_W;
            int height = DEFAULT_H;
            if (w < h) {
                width = DEFAULT_H;
                height = DEFAULT_W;
            } else if (w == h) {
                width = height = DEFAULT_H;
            }
            if (ContextHelper.isGif(url)) {
                ImageHelper.loadImageGif(url, iv_content, width, height);
            } else {
                ImageHelper.loadImageOverrideSize(url, iv_content, width, height);
            }
        } else {
            if (ContextHelper.isGif(url)) {
                ImageHelper.loadImageGif(url, iv_content, DEFAULT_W, DEFAULT_H);
            } else {
                ImageHelper.loadImageOverrideSize(url, iv_content, DEFAULT_W, DEFAULT_H);
            }
        }
    }

}
