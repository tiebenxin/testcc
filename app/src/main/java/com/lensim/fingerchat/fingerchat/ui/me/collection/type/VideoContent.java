package com.lensim.fingerchat.fingerchat.ui.me.collection.type;

import android.content.Context;

import com.lensim.fingerchat.commons.utils.JsonUtils;
import com.lensim.fingerchat.data.me.content.VideoFavContent;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.AbsContentView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.SimpleVideoView;

/**
 * date on 2018/3/21
 * author ll147996
 * describe
 */

public class VideoContent extends Content {

    public VideoContent(String type, String text, boolean isNote) {
        super(type, text, isNote);
    }

    @Override
    public AbsContentView getContentView(Context ctx) {
        return new SimpleVideoView(ctx, this);
    }

}
