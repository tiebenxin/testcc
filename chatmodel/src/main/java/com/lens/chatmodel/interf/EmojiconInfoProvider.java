package com.lens.chatmodel.interf;

import com.lens.chatmodel.bean.Emojicon;
import java.util.Map;

/**
 * Created by LL130386 on 2018/3/2.
 */

public interface EmojiconInfoProvider {
    /**
     * 根据唯一识别号返回此表情内容
     * @param emojiconIdentityCode
     * @return
     */
    Emojicon getEmojiconInfo(String emojiconIdentityCode);

    /**
     * 获取文字表情的映射Map,map的key为表情的emoji文本内容，value为对应的图片资源id或者本地路径(不能为网络地址)
     * @return
     */
    Map<String, Object> getTextEmojiconMapping();

}
