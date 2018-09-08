package com.lensim.fingerchat.fingerchat.ui.me.collection.type;

import android.content.Context;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.AbsContentView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.SimpleVoiceView;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class VoiceContent extends Content {

    public VoiceContent(String type, String text, boolean isNote) {
        super(type, text, isNote);
    }

    @Override
    public AbsContentView getContentView(Context ctx) {
        return new SimpleVoiceView(ctx,this);
    }
}
