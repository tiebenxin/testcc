package com.lensim.fingerchat.commons.helper;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by LL130386 on 2018/5/4.
 * 二维码帮助类
 */

public class CodeHelper {

    public static final int TYPE_NET = 1;
    public static final int TYPE_PRIVATE = 2;
    public static final int TYPE_MUC = 3;

    public static final String DEFAULT_CONTENT = "abcdefghijklmnopqrstuvwxyz&abcdefghijklmnopqrstuvwxyz";
    //    public static final String DEFAULT_CONTENT = "finger";
    public static final String DEFAULT_SPLIT = "::";

    @IntDef({TYPE_NET, TYPE_PRIVATE, TYPE_MUC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ECodeType {

    }

    //生成二维码内容
    public static String createContentText(String userId, @ECodeType int type) {
        StringBuilder builder = new StringBuilder();
        builder.append(type).append(DEFAULT_SPLIT)
            .append(userId).append(DEFAULT_SPLIT)
            .append(DEFAULT_CONTENT);
        return builder.toString();
    }

}
