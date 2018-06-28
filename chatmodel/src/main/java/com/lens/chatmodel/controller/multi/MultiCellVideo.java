package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.Carbon;
import com.lens.chatmodel.bean.VideoEventBean;
import com.lens.chatmodel.cache.FileManager;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lens.chatmodel.ui.video.LookUpVideoActivity;
import com.lensim.fingerchat.data.help_class.IProgressListener;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.io.File;
import java.util.List;

/**
 * Created by LL130386 on 2018/2/1.
 */

public class MultiCellVideo extends MultiCellBase {

    private ImageView iv_content;
    private LinearLayout ll_right;
    private VideoUploadEntity entity;
    private final int DEFAULT_BIG = DensityUtil.dip2px(ContextHelper.getContext(), 160);
    private final int DEFAULT_SMALL = DensityUtil.dip2px(ContextHelper.getContext(), 120);

    protected MultiCellVideo(Context context, EMultiCellLayout cellLayout,
        IChatEventListener listener) {
        super(context, cellLayout, listener);
        loadControls();

    }

    private void loadControls() {
        iv_content = getView().findViewById(R.id.iv_content);
        ll_right = getView().findViewById(R.id.ll_right);
    }

    @Override
    public void onBubbleClick() {
        File file = new File(entity.getVideoUrl());
        if (file.exists()) {
            clickPlay(entity.getVideoUrl());
        }
        String cacheVideoUrl = FileCache.getInstance().getVideoPath(entity.getVideoUrl());
        if (TextUtils.isEmpty(cacheVideoUrl)) {
            download();
        } else {
            clickPlay(cacheVideoUrl);
        }
    }

    private void clickPlay(String path) {
        AnimationRect rect = AnimationRect.buildFromImageView(iv_content);
        Intent intent = LookUpVideoActivity
            .newIntent(mContext, rect, path, "chat");
        mContext.startActivity(intent);
    }

    @Override
    public void showData() {
        super.showData();
        if (mEntity != null) {
            entity = VideoUploadEntity.fromJson(mEntity.getBody());
            if (entity != null) {
                loadImage(entity.getImageUrl(), entity.getImageSize());
            }
        }
        updateWidth();
    }

    private void updateWidth() {
        float screenWidth = TDevice.getScreenWidth();
        int avatarWidth = iv_avatar.getMeasuredWidth();
        LayoutParams avatarParams = (LayoutParams) iv_avatar.getLayoutParams();
        int avatarMargin = avatarParams.leftMargin + avatarParams.rightMargin;
        LayoutParams rightParams = (LayoutParams) ll_right.getLayoutParams();
        int rightMargin = rightParams.rightMargin + rightParams.leftMargin;
        int width = (int) (screenWidth - avatarMargin - avatarWidth - rightMargin);
        ll_right.setMinimumWidth(width);
    }

    private void download() {
        if (entity != null && !TextUtils.isEmpty(entity.getVideoUrl())) {
            FileManager.getInstance()
                .downloadFile(entity.getVideoUrl(), EUploadFileType.VIDEO, new IProgressListener() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                    }

                    @Override
                    public void progress(int progress) {

                    }

                    @Override
                    public void onFailed() {

                    }
                });
        }
    }

    private void loadImage(String url, String size) {
        if (!TextUtils.isEmpty(url)) {
            List<Integer> arrs = ImageHelper.getImageSize(size);
            if (arrs != null && arrs.size() == 2) {
                int width = arrs.get(0);
                int height = arrs.get(1);
                if (width < height) {
                    width = DEFAULT_SMALL;
                    height = DEFAULT_BIG;
                } else {
                    width = DEFAULT_BIG;
                    height = DEFAULT_SMALL;
                }
                ImageHelper.loadImageOverrideSize(url, iv_content, width, height);
            } else {
                ImageHelper.loadImageOverrideSize(url, iv_content, DEFAULT_BIG, DEFAULT_SMALL);
            }
        } else {
            ImageHelper.loadImageOverrideSize(url, iv_content, DEFAULT_BIG, DEFAULT_SMALL);
        }

    }
}
