package com.lensim.fingerchat.fingerchat.ui.me.collection.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lens.chatmodel.base.ChatEnvironment;
import com.lens.chatmodel.bean.Emojicon;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;

/**
 * date on 2018/3/14
 * author ll147996
 * describe
 */

public class BigImageView implements AbsContentView {
    private final int DEFAULT_W = DensityUtil.dip2px(ContextHelper.getContext(), 200);
    private final int DEFAULT_H = DensityUtil.dip2px(ContextHelper.getContext(), 120);

    private ImageView simpleImage;
    private String url;
    Context mContext;

    public BigImageView(Context ctx, Content content) {
        mContext = ctx;
        url = content.getText();
    }

    private void setSimpleImage() {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Emojicon emojicon;
        if (ChatEnvironment.getInstance().getEmojiconInfoProvider() != null) {
            emojicon = ChatEnvironment.getInstance().getEmojiconInfoProvider()
                .getEmojiconInfo(url);
            if (emojicon != null) {
                if (emojicon.getBigIcon() != 0) {
                    ImageHelper
                        .loadImageOverrideSize(emojicon.getBigIcon(), simpleImage, DEFAULT_W,
                            DEFAULT_H);
                } else if (emojicon.getBigIconPath() != null) {
                    Glide.with(ContextHelper.getContext())
                        .load(emojicon.getBigIconPath())
                        .asGif()
                        .placeholder(R.drawable.ease_default_expression)
                        .into(simpleImage);
                } else {
                    simpleImage.setImageResource(R.drawable.ease_default_expression);
                }
            } else {
                ImageUploadEntity entity = ImageUploadEntity
                    .fromJson(url);
                if (entity != null) {
                    Glide.with(ContextHelper.getContext()).load(entity.getOriginalUrl()).asGif().into(simpleImage);
                } else {
                    simpleImage.setImageResource(R.drawable.ease_default_expression);
                }
            }
        }
    }

    @Override
    public View getFrameLayoutView(LayoutInflater mInflater, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.viewstub_collect_image, parent, false);
        simpleImage = view.findViewById(R.id.simple_image);
        setSimpleImage();
        return view;
    }
}
