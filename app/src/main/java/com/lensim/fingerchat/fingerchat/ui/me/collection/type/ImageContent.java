package com.lensim.fingerchat.fingerchat.ui.me.collection.type;

import android.content.Context;

import com.lensim.fingerchat.commons.utils.JsonUtils;
import com.lensim.fingerchat.data.me.content.FavContent;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.AbsContentView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.SimpleImageView;

import org.json.JSONObject;

/**
 * date on 2018/3/21
 * author ll147996
 * describe
 */

public class ImageContent extends Content {

    public ImageContent(String type, String text, boolean isNote) {
        super(type, text, isNote);
    }

    @Override
    public AbsContentView getContentView(Context ctx) {
        toUrl();
        return new SimpleImageView(ctx, this);
    }

    private void toUrl() {
        FavContent favContent = JsonUtils.fromJson(getText(), FavContent.class);
        if (favContent == null){
            return;
        }
        setText(favContent.getOriginalUrl());
    }
}
