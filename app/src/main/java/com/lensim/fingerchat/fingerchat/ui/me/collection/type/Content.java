package com.lensim.fingerchat.fingerchat.ui.me.collection.type;

import android.content.Context;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.AbsContentView;

/**
 * date on 2018/3/21
 * author ll147996
 * describe
 */

public abstract class Content {

    private String type;
    private String text;
    private boolean isNote;

    public boolean isNote() {
        return isNote;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Content(String type, String text, boolean isNote) {
        this.type = type;
        this.text = text;
        this.isNote = isNote;
    }


    public abstract AbsContentView getContentView(Context ctx);

    public static final int MSG_TYPE_TEXT = EMessageType.TEXT.value;
    public static final int MSG_TYPE_PIC = EMessageType.IMAGE.value;
    public static final int MSG_TYPE_BIG_EX = EMessageType.FACE.value;
    public static final int MSG_TYPE_VIDEO = EMessageType.VIDEO.value;
    public static final int MSG_TYPE_CONTENT = EMessageType.CONTACT.value;
    public static final int MSG_TYPE_NOTE = 88;

    /***
     * 1.text 文字
     2.image 图片
     3.video 视频
     4.gif 大表情
     * */
    public static int getMsgType(String type) {
        switch (type) {
            case "1":
                return EMessageType.TEXT.ordinal();
            case "2":
                return EMessageType.IMAGE.ordinal();
            case "3":
                return EMessageType.VIDEO.ordinal();
            case "4":
                return EMessageType.FACE.ordinal();
            case "5":
                return EMessageType.CONTACT.ordinal();
            default:
                return MSG_TYPE_NOTE;
        }
    }
}
