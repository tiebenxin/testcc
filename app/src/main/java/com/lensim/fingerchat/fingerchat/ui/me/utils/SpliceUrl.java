package com.lensim.fingerchat.fingerchat.ui.me.utils;

import android.support.annotation.NonNull;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lensim.fingerchat.commons.global.Route;
import java.util.ArrayList;

/**
 * date on 2018/3/20
 * author ll147996
 * describe
 */

public class SpliceUrl {

    public static String getUrl(@NonNull String url) {
        if (url.toLowerCase().startsWith("hnlensimage") || url.toLowerCase()
            .startsWith("/hnlensimage")) {
            url = Route.Host + url;
        }
        return url;
    }

    public static ArrayList<String> getUrls(@NonNull ArrayList<String> urls) {
        ArrayList<String> list = new ArrayList<>();
        for (String url : urls) {
            list.add(ImageUploadEntity.toJson(ImageUploadEntity.createEntity(url)));
        }
        return list;
    }

}
