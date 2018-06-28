package com.lensim.fingerchat.fingerchat.ui.me.collection.type;

import android.content.Context;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.AbsContentView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.NoteView;

/**
 * date on 2018/3/21
 * author ll147996
 * describe
 */

public class NoteContent extends Content {

    public NoteContent(String type, String text, boolean isNote) {
        super(type, text, isNote);
    }

    @Override
    public AbsContentView getContentView(Context ctx) {
        return new NoteView(ctx, this);
    }

}
