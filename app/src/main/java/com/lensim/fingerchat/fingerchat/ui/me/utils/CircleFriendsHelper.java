package com.lensim.fingerchat.fingerchat.ui.me.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import java.util.List;


/**
 * Created by LL130386 on 2017/7/21.
 *
 */

public class CircleFriendsHelper<T> {

    public static <T> boolean isEmpty(List<T> list) {
        if (list == null || list.size() <= 0) {
            return true;
        }
        return false;
    }

    public static long getBitmapsize(Bitmap bitmap) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

    }

    /*
    * 获取朋友圈主题背景
    * */
    public static String getThemePath(String user) {
        String themePath;
        if (!TextUtils.isEmpty(user) && user.equals(UserInfoRepository.getUserName())) {
            themePath = SPSaveHelper.getStringValue(UserInfoRepository.getUserName() + AppConfig.CIRCLE_THEME_PATH, "");
            if (StringUtils.isEmpty(themePath)) {
                themePath = String.format(Route.obtainTheme, UserInfoRepository.getUserName());
            } else {
                if (!themePath.startsWith("http")){
                themePath = ImageDownloader.Scheme.FILE.wrap(themePath);
                }
            }
        } else {
            themePath = String.format(Route.obtainTheme, user);
        }
        return themePath;
    }
}
