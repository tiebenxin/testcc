package com.lensim.fingerchat.fingerchat.ui.me.collection.type;

import com.lensim.fingerchat.fingerchat.ui.me.utils.SpliceUrl;

/**
 * date on 2018/3/21
 * author ll147996
 * describe
 */

public class ContentFactory {

    public static Content createContent(String type, String text) {
        Content content;
        //1.text 文字 2.image 图片 3.video 视频 4.gif 大表情
        if ("1".equals(type)) {
            content = new TextContent(type, text, false);
        } else if ("2".equals(type)) {
            content = new ImageContent(type, SpliceUrl.getUrl(text), false);
        } else if ("3".equals(type)) {
            content = new VideoContent(type, SpliceUrl.getUrl(text), false);
        } else if ("4".equals(type)) {
            content = new BigImageContent(type, SpliceUrl.getUrl(text), false);
        } else if ("5".equals(type)){
            content = new ContactContent(type, text, false);
        }else {
            content = new NoteContent(type, text, true);
        }
        return content;
    }
}
