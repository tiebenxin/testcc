package com.lens.chatmodel.view.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by LL130386 on 2018/1/16.
 */

public class ImageLinearLayout extends LinearLayout {

    private ImageView view;
    private String msgId;
    boolean isLongImage;

    public ImageLinearLayout(Context context) {
        super(context);
    }

    public ImageLinearLayout(Context context,
        @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageView(ImageView v){
        view = v;
    }

    public ImageView getImageView(){
        return view;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }


    public boolean isLongImage() {
        return isLongImage;
    }

    public void setLongImage(boolean longImage) {
        isLongImage = longImage;
    }


}
