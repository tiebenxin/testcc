package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.ChatEnvironment;
import com.lens.chatmodel.bean.Emojicon;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.util.ArrayList;

/**
 * Created by LL130386 on 2018/2/1.
 */

public class MultiCellEmoticon extends MultiCellBase {

    private ImageView iv_content;
    private LinearLayout ll_right;
    private final int DEFAULE_W = DensityUtil.dip2px(ContextHelper.getContext(), 200);
    private final int DEFAULT_H = DensityUtil.dip2px(ContextHelper.getContext(), 120);

    protected MultiCellEmoticon(Context context, EMultiCellLayout cellLayout,
        IChatEventListener listener) {
        super(context, cellLayout, listener);
        loadControls();

    }

    private void loadControls() {
        iv_content = getView().findViewById(R.id.iv_content);
        ll_right = getView().findViewById(R.id.ll_right);
    }

    @Override
    public void showData() {
        super.showData();
        if (mEntity != null) {
            Emojicon emojicon = null;
            if (ChatEnvironment.getInstance().getEmojiconInfoProvider() != null) {
                emojicon = ChatEnvironment.getInstance().getEmojiconInfoProvider()
                    .getEmojiconInfo(mEntity.getBody());
                if (emojicon != null && emojicon.getBigIcon() != 0) {
                    ImageHelper
                        .loadImageOverrideSize(emojicon.getBigIcon(), iv_content, DEFAULE_W,
                            DEFAULT_H);
                } else {
                    ImageUploadEntity entity = ImageUploadEntity.fromJson(mEntity.getBody());
                    if (entity != null) {
                        loadImage(entity.getOriginalUrl(), entity.getOriginalSize());
                    }
                }
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

    private void loadImage(String url, String size) {
        if (!TextUtils.isEmpty(size)) {
            String[] arr = size.split("x");
            if (arr == null || arr.length != 2) {
                load(url);
                return;
            }
            int w = Integer.parseInt(arr[0]);
            int h = Integer.parseInt(arr[1]);
            int width = DEFAULE_W;
            int height = DEFAULT_H;
            if (w < h) {
                width = DEFAULT_H;
                height = DEFAULE_W;
            } else if (w == h) {
                width = height = DEFAULT_H;
            }

            if (ContextHelper.isGif(url)) {
                ImageHelper.loadImageGif(url, iv_content, width, height);
            } else {
                ImageHelper.loadImageOverrideSize(url, iv_content, width, height);
            }
        } else {
            load(url);
        }

    }

    private void load(String url) {
        if (ContextHelper.isGif(url)) {
            ImageHelper.loadImageGif(url, iv_content, DEFAULE_W, DEFAULT_H);
        } else {
            ImageHelper.loadImageOverrideSize(url, iv_content, DEFAULE_W, DEFAULT_H);
        }
    }

    @Override
    public void onBubbleClick() {
        ArrayList<AnimationRect> animationRectArrayList = new ArrayList<AnimationRect>();
        AnimationRect rect = AnimationRect.buildFromImageView(iv_content);
        if (rect != null) {
            rect.setUri(mEntity.getBody());
        }
        animationRectArrayList.add(rect);
        ArrayList<String> urls = new ArrayList<>();
        urls.add(mEntity.getBody());
        Intent intent = GalleryAnimationActivity
            .newIntent(urls, null, animationRectArrayList, null, 0,"");
        mContext.startActivity(intent);
    }
}
