package com.lensim.fingerchat.commons.helper;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/1/17.
 */

public class EmojiHelper {

    public static String listToString(List<String> list) {
        if (list == null || list.size() <= 0) {
            return "";
        }
        int len = list.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                builder.append(";");
            }
            builder.append(list.get(i));
        }
        return builder.toString();
    }

    public static List<String> stringToList(String array) {
        if (TextUtils.isEmpty(array)) {
            return null;
        }
        String[] arrs = array.split(";");
        if (arrs == null || arrs.length <= 0) {
            return null;
        }
        int len = arrs.length;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            list.add(arrs[i]);
        }
        return list;
    }

}
