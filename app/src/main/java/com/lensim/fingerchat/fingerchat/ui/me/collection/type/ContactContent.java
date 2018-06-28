package com.lensim.fingerchat.fingerchat.ui.me.collection.type;

import android.content.Context;

import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.AbsContentView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.SimpleContactView;

public class ContactContent extends Content{

    public ContactContent(String type, String text, boolean isNote) {
        super(type, text, isNote);
    }

    @Override
    public AbsContentView getContentView(Context ctx) {
        return new SimpleContactView(ctx, getText());
    }
}
