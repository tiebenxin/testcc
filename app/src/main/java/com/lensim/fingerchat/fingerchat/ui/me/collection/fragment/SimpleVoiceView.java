package com.lensim.fingerchat.fingerchat.ui.me.collection.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lensim.fingerchat.commons.utils.JsonUtils;
import com.lensim.fingerchat.data.me.content.VoiceFavContent;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class SimpleVoiceView implements AbsContentView {
    private TextView tv_length;
    Context context;
    private VoiceFavContent favContent;
    public SimpleVoiceView(Context ctx, Content content){
       this.context = ctx;
        favContent = JsonUtils.fromJson(content.getText(), VoiceFavContent.class);
    }

    private void setSimpleVoice(){
        if (favContent == null || TextUtils.isEmpty(favContent.getVoiceLenth())) {
            return;
        }
        tv_length.setText(favContent.getVoiceLenth()+"'s");
    }

    @Override
    public View getFrameLayoutView(LayoutInflater mInflater, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.viewstub_collect_voice, parent, false);
        tv_length = view.findViewById(R.id.tv_length);
        setSimpleVoice();
        return view;
    }
}
