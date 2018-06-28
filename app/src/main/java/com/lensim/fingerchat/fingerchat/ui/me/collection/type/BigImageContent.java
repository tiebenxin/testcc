package com.lensim.fingerchat.fingerchat.ui.me.collection.type;

import android.content.Context;

import com.lensim.fingerchat.commons.utils.JsonUtils;
import com.lensim.fingerchat.data.me.content.FavContent;
import com.lensim.fingerchat.data.me.content.GifFavContent;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.AbsContentView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.BigImageView;

/**
 * date on 2018/3/21
 * author ll147996
 * describe
 */

public class BigImageContent extends Content {

    public BigImageContent(String type, String text, boolean isNote) {
        super(type, text, isNote);
    }

    @Override
    public AbsContentView getContentView(Context ctx) {
        GifFavContent favContent = JsonUtils.fromJson(getText(), GifFavContent.class);
        setText(favContent.getBody());
        return new BigImageView(ctx, this);
    }

}
